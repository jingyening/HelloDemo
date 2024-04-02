package com.bruce.jing.hello.demo.frame.monitor.block

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.bruce.jing.hello.demo.App
import com.bruce.jing.hello.demo.frame.monitor.msg.MessageCollect
import com.bruce.jing.hello.demo.util.log.JLogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date


/**
 * 监测卡顿
 * 主线程发送消息，检测延时后收集以下信息
 * 1、MessageQueue
 * 2、主线程调用堆栈
 * 3、trace信息（应该涵盖了主线程调用堆栈）
 * 4、CPU信息（可选）
 * 5、整机、APP内存信息（可选）
 * 6、system trace信息（可选）
 * 7、进程内存快照（可选）
 *
 */
@RequiresApi(Build.VERSION_CODES.M)
object BlockMonitor {
    const val TAG = "BlockMonitor"

    const val DIR_BLOCK_MONITOR = "block_monitor/"
    const val FILE_MESSAGE_INFO = "message_info"
    const val FILE_ALL_TRACE = "trace_all_thread"
    const val FILE_MAIN_TRACE = "trace_main_thread"
    const val FILE_SELF_CPU = "cpu_info_self"
    const val FILE_PROCESS_CPU = "cpu_info_process"
    const val FILE_PREVIOUS_CPU = "cpu_info_previous"
    const val FILE_SELF_MEMORY = "memory_info_self"
    const val FILE_ALL_MEMORY = "memory_info_whole"
    const val FILE_SYSTEM_TRACE = "system_trace"

    private var MSG_DELAY_THRESHOLD = 1000L

    private const val MSG_MONITOR_BLOCK_SIGNAL = 666666
    private const val MSG_SEND_SIGNAL = 2
    private const val MSG_CHECK_SIGNAL = 3
    private var mainThread:Thread ?= null

    private val handler = Handler(Looper.getMainLooper()){
        when(it.what){
            MSG_MONITOR_BLOCK_SIGNAL ->{
                if(mainThread == null){
                    mainThread = Thread.currentThread()
                }
            }
        }
        true
    }
    private val subHandlerThread = HandlerThread("BlockMonitor")
    private lateinit var subHandler:Handler

    @Volatile
    private var monitorEnable = false
    init {
        subHandlerThread.start()
        subHandler = Handler(subHandlerThread.looper){
            when(it.what){
                MSG_SEND_SIGNAL ->{
                    handler.sendEmptyMessage(MSG_MONITOR_BLOCK_SIGNAL)
                    subHandler.sendEmptyMessageDelayed(MSG_CHECK_SIGNAL, MSG_DELAY_THRESHOLD)
                }
                MSG_CHECK_SIGNAL ->{
                    checkBlockSignal()
                }
            }
            true
        }
    }

    private fun checkBlockSignal() {
        if (!monitorEnable) {
            return
        }
        if (handler.hasMessages(MSG_MONITOR_BLOCK_SIGNAL)) {
            handler.removeMessages(MSG_MONITOR_BLOCK_SIGNAL)
            //ui thread block at least 1s
            snapshot()
        }
        if (!monitorEnable) {
            return
        }
        subHandler.removeMessages(MSG_SEND_SIGNAL)
        subHandler.sendEmptyMessage(MSG_SEND_SIGNAL)
    }

    fun captureSnapshot(){
        android.util.Log.d(TAG,"captureSnapshot")
        snapshot()
    }

    private fun snapshot() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val start = SystemClock.elapsedRealtime()
                val rootDir = App.getGlobalContext().externalCacheDir
//                val rootDir = Environment.getExternalStorageDirectory()
                rootDir?: return@launch
                JLogUtil.d("$TAG rootDir = ${rootDir.absolutePath}")
                val timeDir = getFormatTime()
                val parentDir =
                    rootDir.absolutePath + File.separatorChar + DIR_BLOCK_MONITOR + timeDir
                if (!File(parentDir).exists()) {
                    File(parentDir).mkdirs()
                }

                writeMessageInfo(parentDir + File.separatorChar + FILE_MESSAGE_INFO)

                mainThread?.let {
                    val trace = it.stackTrace
                    writeMainTrace(trace, parentDir + File.separatorChar + FILE_MAIN_TRACE)
                }
                val traces = Thread.getAllStackTraces()
                writeAllTrace(traces, parentDir + File.separatorChar + FILE_ALL_TRACE)
                dumpCpuInfo(parentDir)
                dumpMemoryInfo(parentDir)
//                dumpSystemTrace(parentDir)
                JLogUtil.d("$TAG snapshot cost time = ${SystemClock.elapsedRealtime() - start}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun writeMessageInfo(path:String) {
        val list = MessageCollect.collectMessage()
        MessageCollect.writeMessageInfoToFile(list, path)
    }

    private fun writeMainTrace(trace:Array<StackTraceElement>, path:String) {
        var printWriter: PrintWriter? = null
        try {
            printWriter = PrintWriter(File(path))
            for (traceElement in trace) {
                printWriter.println("\tat $traceElement")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            try {
                printWriter?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun writeAllTrace(traces: Map<Thread, Array<StackTraceElement>>, path: String) {
        val start = SystemClock.elapsedRealtime()
        var printWriter: PrintWriter? = null
        try {
            printWriter = PrintWriter(File(path))
            traces.forEach{
                val thread = it.key
                printWriter.println("thread name = ${thread.name}, tid = ${thread.id}, state = ${thread.state}")
                val trace = it.value
                for (traceElement in trace) {
                    printWriter.println("\tat $traceElement")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            try {
                printWriter?.close()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        JLogUtil.d("$TAG writeAllTrace cost time = ${SystemClock.elapsedRealtime() - start}")
    }

    /**
     * dump:
     * 当前进程每个线程使用cpu的情况；
     * 整个系统进程使用cpu的情况；
     * 过去一段时间系统使用CPU的情况；
     */
    private fun dumpCpuInfo(parentDir:String) {
        val startTime = SystemClock.elapsedRealtime()
        val pid = Process.myPid()
        val selfPath = parentDir+File.separatorChar + FILE_SELF_CPU
        val selfCommand = "ps $pid -T -o NAME,TID,PRI,%CPU,CMD,CPU"
        executeCommandAndWrite(selfCommand,selfPath)

        val processPath = parentDir+File.separatorChar+ FILE_PROCESS_CPU
        val processCommand = "top -b -n 1"
        executeCommandAndWrite(processCommand, filePath = processPath)

        val previousPath = parentDir+File.separatorChar+ FILE_PREVIOUS_CPU
//        val previousCommand = "dumpsys cpuinfo"
        val previousCommand = "cat /proc/stat"
        executeCommandAndWrite(previousCommand, filePath = previousPath)
        JLogUtil.d("$TAG dumpCpuInfo cost time = ${SystemClock.elapsedRealtime() - startTime}")

    }


    /**
     * dump：
     * 当前进程memory信息
     * 系统memory信息
     */
    private fun dumpMemoryInfo(parentDir:String){
        val startTime = SystemClock.elapsedRealtime()
        val packageName = App.getGlobalContext().packageName
        val pid = Process.myPid()
        val selfPath = parentDir+File.separatorChar + FILE_SELF_MEMORY
        val selfCommand = "dumpsys meminfo $pid"
//        val selfCommand = "cat /proc/$pid/mem"
//        executeCommandAndWrite(selfCommand,selfPath)

        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        writeMemoryInfoToFile(memoryInfo,selfPath)

        val allPath = parentDir + File.separatorChar + FILE_ALL_MEMORY
        val allCommand = "dumpsys meminfo"
//        val allCommand = "cat  /proc/meminfo"
        executeCommandAndWrite(allCommand, allPath)

        JLogUtil.d("$TAG dumpMemoryInfo cost time = ${SystemClock.elapsedRealtime() - startTime}")
    }


    private fun writeMemoryInfoToFile(memoryInfo: Debug.MemoryInfo, filePath: String){
        var printWriter:PrintWriter ?= null
        try {
            printWriter = PrintWriter(filePath)
            val memoryStats = memoryInfo.memoryStats
            memoryStats.forEach {
                printWriter.println("${it.key} : ${it.value}kb")
            }
            val maxMemory = Runtime.getRuntime().maxMemory() / 1024
            printWriter.println("maxMemory = ${maxMemory}kb")
            val totalMemory = Runtime.getRuntime().totalMemory() / 1024
            printWriter.println("totalMemory = ${totalMemory}kb")
            val freeMemory = Runtime.getRuntime().freeMemory() / 1024
            printWriter.println("freeMemory = ${freeMemory}kb")
            val usedMemory = totalMemory - freeMemory
            printWriter.println("usedMemory = ${usedMemory}kb")
            val leftMemory = maxMemory - usedMemory
            printWriter.println("leftMemory = ${leftMemory}kb")
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            try {
                printWriter?.close()
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    private fun dumpSystemTrace(parentDir: String){
        val startTime = SystemClock.elapsedRealtime()
        val tracePath = parentDir+File.separatorChar+ FILE_SYSTEM_TRACE
        val command = "atrace sched gfx view wm am aidl"
        executeCommandAndWrite(command,tracePath)
        JLogUtil.d("$TAG dumpSystemTrace cost time = ${SystemClock.elapsedRealtime() - startTime}")
    }

    private fun executeCommandAndWrite(command:String, filePath:String):Boolean{
        var output:PrintWriter ?= null
        var contentReader:BufferedReader ?= null
        var errorReader:BufferedReader ?= null
        try {
             val process = Runtime.getRuntime().exec(command)
            // 读取命令的输出
            contentReader = BufferedReader(InputStreamReader(process.inputStream))
            errorReader = BufferedReader(InputStreamReader(process.errorStream))

            var line: String?
            output = PrintWriter(filePath)
            while (contentReader.readLine().also { line = it } != null) {
                output.println(line)
            }
            var errorLine:String ?
            while (errorReader.readLine().also { errorLine = it } != null) {
                output.println(errorLine)
            }
            // 等待命令执行完成
            val exitCode = process.waitFor()
            JLogUtil.d("$TAG executeCommandAndWrite command = $command, exitCode = $exitCode")
            //命令是否执行成功
            return exitCode == 0

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }finally {
            try {
                // 关闭BufferedReader
                contentReader?.close()
                errorReader?.close()
                output?.close()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun executeCommandAndWrite2(vararg command: String, filePath:String):Boolean{

        var output:PrintWriter ?= null
        var contentReader:BufferedReader ?= null
        var errorReader:BufferedReader ?= null
        try {
            // 创建 ProcessBuilder 实例
            val processBuilder = ProcessBuilder(command.asList())
            val process: java.lang.Process = processBuilder.start()
            //// 读取命令的输出
            contentReader = BufferedReader(InputStreamReader(process.inputStream))
            // 读取错误的输出
            errorReader = BufferedReader(InputStreamReader(process.errorStream))

            var line: String?
            output = PrintWriter(filePath)
            while (contentReader.readLine().also { line = it } != null) {
                output.println(line)
            }
            var errorLine:String ?
            while (errorReader.readLine().also { errorLine = it } != null) {
                output.println(errorLine)
            }
            // 等待命令执行完成
            val exitCode = process.waitFor()
            JLogUtil.d("$TAG executeCommandAndWrite2 exitCode = $exitCode")
            //命令是否执行成功
            return exitCode == 0

        }catch (e:Exception){
            e.printStackTrace()
            return false
        }finally {
            try {
                // 关闭BufferedReader
                contentReader?.close()
                errorReader?.close()
                output?.close()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun startMonitor(blockThreshold:Int = 1000){
        MSG_DELAY_THRESHOLD = blockThreshold.toLong()
        monitorEnable = true
        subHandler.removeCallbacksAndMessages(null)
        subHandler.sendEmptyMessage(MSG_SEND_SIGNAL)
    }

    fun stopMonitor(){
        monitorEnable = false
        subHandler.removeCallbacksAndMessages(null)
    }


    private fun getFormatTime():String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(Date(System.currentTimeMillis()))
    }
}