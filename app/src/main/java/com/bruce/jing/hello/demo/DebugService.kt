package com.bruce.jing.hello.demo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.bruce.jing.hello.demo.frame.monitor.msg.MessageCollect
import com.bruce.jing.hello.demo.util.log.JLogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileDescriptor
import java.io.PrintWriter
import java.util.ArrayList

/**
 * adb shell am startservice -a hmi_debug_service_debug
 * adb shell dumpsys activity service com.baidu.adt.hmi.passenger.debug.DebugService 1 1 xxx
 */
class DebugService: Service() {

    companion object{
        const val TAG = "DebugService"
    }

    private val list  = ArrayList<String>()

    override fun onCreate() {
        super.onCreate()
        JLogUtil.d("$TAG onCreate")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    /**
     * function:
     * 0-->dump log
     * 1-->
     */
    override fun dump(fd: FileDescriptor?, writer: PrintWriter?, args: Array<out String>?) {
        super.dump(fd, writer, args)
        if(!BuildConfig.DEBUG){
            return
        }
        args?.forEachIndexed { index, s ->
            JLogUtil.d("$TAG dump args index = $index, s = $s")

        }

        val function =args?.get(0)?.toInt()

        when(function){
            0->{
                val arg1 = args?.get(1)?.toInt()
                dumpLogStatistic(arg1)
            }
            1->{
                dumpMsg()
            }
        }
    }

    private fun dumpMsg() {
        GlobalScope.launch(Dispatchers.IO) {
            val list = MessageCollect.collectMessage()
            JLogUtil.d(TAG, "collectMsg msg size = ${list.size}")
            list.forEachIndexed { index, messageInfo ->
                JLogUtil.d(TAG, "collectMsg index = $index messageInfo = $messageInfo")
            }
        }
    }

    private fun dumpLogStatistic(arg1: Int?) {
        if (arg1 == 0) {
            JLogUtil.dumpLogStatisticSortByLines()
        } else if (arg1 == 1) {
            JLogUtil.dumpLogStatisticSortBySize()
        } else if (arg1 == 2) {
            JLogUtil.clearDump()
        }
    }
}