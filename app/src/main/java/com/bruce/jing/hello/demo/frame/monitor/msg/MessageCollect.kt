package com.bruce.jing.hello.demo.frame.monitor.msg

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.MessageQueue
import android.util.Log
import com.bruce.jing.hello.demo.util.log.JLogUtil
import java.io.File
import java.io.PrintWriter

object MessageCollect {

    const val TAG = "MessageCollect"

    @SuppressLint("SoonBlockedPrivateApi")
    fun collectMessage():List<MessageInfo>{
        val result = BootstrapClass.exemptAll()
        Log.d(TAG, "result = $result")
        val list = ArrayList<MessageInfo>()
        val handler = Handler(Looper.getMainLooper())
        val field = Handler::class.java.getDeclaredField("mQueue")
        field.isAccessible = true
        val queue = field.get(handler) as MessageQueue

        val queueField = MessageQueue::class.java.getDeclaredField("mMessages")
        queueField.isAccessible = true
        val head = queueField.get(queue) as Message
        var p: Message? = head
        while (p != null) {
            getMessageInfo(list,p)
            val nextField = Message::class.java.getDeclaredField("next")
            nextField.isAccessible = true
            val next = getNextMessage(p)
            p = next
        }
        return list
    }

    private fun getMessageInfo(list: ArrayList<MessageInfo>, message: Message) {
        val messageInfo = MessageInfo()
        messageInfo.what = message.what
        messageInfo.msgWhen = message.`when`
        messageInfo.target = message.target?.toString()?:"null"
        messageInfo.callback = message.callback?.toString()?:"null"
        list.add(messageInfo)
    }


    private fun getNextMessage(message: Message):Message?{
        val nextField = Message::class.java.getDeclaredField("next")
        nextField.isAccessible = true
        val next = nextField.get(message) as? Message
        return next
    }

    fun writeMessageInfoToFile(infoList: List<MessageInfo>, filePath: String){
        if(infoList.isEmpty()){
            JLogUtil.d("$TAG writeMessageInfoToFile infoList is empty")
            return
        }
        val file = File(filePath)
        if(!file.exists()){
            file.parentFile?.mkdirs()
        }
        var printWriter:PrintWriter ?= null
        try {
            printWriter = PrintWriter(file)
            infoList.forEachIndexed { index, messageInfo ->
                printWriter.println("index = $index, messageInfo = {$messageInfo}")
            }
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

    private fun getMessageWhen(message: Message):Long?{
        val whenField = Message::class.java.getDeclaredField("when")
        whenField.isAccessible = true
        val time = whenField.get(message) as? Long
        return time
    }

    private fun getMessageWhat(message: Message):Int?{
        val whatField = Message::class.java.getDeclaredField("what")
        whatField.isAccessible = true
        val what = whatField.get(message) as? Int
        return what
    }

    private fun getMessageTarget(message: Message):Handler?{
        val targetField = Message::class.java.getDeclaredField("target")
        targetField.isAccessible = true
        val target = targetField.get(message) as? Handler
        return target
    }

    private fun getMessageCallback(message: Message):Runnable?{
        val callbackField = Message::class.java.getDeclaredField("callback")
        callbackField.isAccessible = true
        val callback = callbackField.get(message) as? Runnable
        return callback

    }

}