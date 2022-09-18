package com.example.hidsend

import android.content.Intent
import android.os.*
import com.topjohnwu.superuser.ipc.RootService


class MyRootService : RootService(), Handler.Callback {

    companion object {

        init {
            System.loadLibrary("hidsend")
        }
        
        const val MSG_SEND_MESSAGE = 1
        const val MSG_SEND_RAW_COMMANDS = 3

    }

    private external fun sendMessage(msg: String): Int


    private lateinit var mMessenger: Messenger

    override fun onBind(intent: Intent): IBinder {
        val handler = Handler(Looper.getMainLooper(), this)
        mMessenger = Messenger(handler)
        return mMessenger.binder
    }

    override fun handleMessage(msg: Message): Boolean {
        return when (msg.what) {
            MSG_SEND_MESSAGE -> {

                for (char in msg.data["msg"].toString()) {
                    sendMessage(parseKey(char))
                }

                if(msg.replyTo != null) {
                    val replyMessage = Message.obtain(null, MainActivity.MSG_RECEIVED)
                    Bundle().also { bundle ->
                        bundle.putString("msg", "Received: ${msg.data["msg"]}")
                        replyMessage.data = bundle
                    }
                    msg.replyTo.send(replyMessage)
                }
                true
            }
            MSG_SEND_RAW_COMMANDS -> {
                for( line in msg.data["msg"].toString().lines()){

                    if(msg.replyTo != null) {
                        val replyMessage = Message.obtain(null, MainActivity.MSG_RECEIVED)
                        Bundle().also { bundle ->
                            bundle.putString("msg", "Received: $line")
                            replyMessage.data = bundle
                        }
                        msg.replyTo.send(replyMessage)
                    }

                    sendMessage(line)
                }
                true
            }
            else -> false
        }

    }

    private fun parseKey(char: Char): String {
        if(char.isDigit()){
            return char.toString()
        }
        else if(char.isWhitespace()){
            return "space"
        }
        else if(char.isLetter()) {
            if(char.isUpperCase()){
                return "left-shift ${char.lowercase()}"
            }
           return char.toString()
        }else {
            return if(!Characters.conversionMap.containsKey(char)) char.toString()
            else Characters.conversionMap[char]!!
        }
    }

}