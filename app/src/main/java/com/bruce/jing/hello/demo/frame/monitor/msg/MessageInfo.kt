package com.bruce.jing.hello.demo.frame.monitor.msg

import android.os.SystemClock

class MessageInfo {
    var what:Int ?= null
    var msgWhen:Long ?= null
    var target: String ?= null
    var callback:String ?= null

    override fun toString(): String {
        return "what = $what , when = ${transMsgWhen(msgWhen)}, target = $target, callback = $callback"
    }


    private fun transMsgWhen(msgTime:Long?):String{
        msgTime?:return ""
        val current = SystemClock.uptimeMillis()
        val time =  current - msgTime;
        return if(time > 0){
            "延迟 $time 毫秒"
        }else{
            "${-time}毫秒后执行 "
        }
    }

    override fun equals(other: Any?): Boolean {
        other?:return false
        if(other !is MessageInfo) return false
        if(this == other)return true
        if(this.target == other.target && this.what == other.what){
            return true
        }
        return false
    }
}