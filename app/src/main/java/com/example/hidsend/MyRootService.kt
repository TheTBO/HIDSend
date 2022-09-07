package com.example.hidsend

import android.content.Intent
import android.os.*
import com.topjohnwu.superuser.ipc.RootService


class MyRootService : RootService(), Handler.Callback {

    companion object {
        init {
            System.loadLibrary("hidsend")
        }
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
                    val replyMessage = Message.obtain(null, MSG_RECEIVED)
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
                        val replyMessage = Message.obtain(null, MSG_RECEIVED)
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
            return when(char){
                '`' -> "backquote"
                '~' -> "tilde"
                '!' -> "left-shift 1"
                '@' -> "left-shift 2"
                '#' -> "left-shift 3"
                '$' -> "left-shift 4"
                '%' -> "left-shift 5"
                '^' -> "left-shift 6"
                '&' -> "left-shift 7"
                '*' -> "left-shift 8"
                '(' -> "left-shift 9"
                ')' -> "left-shift 0"
                '-' -> "minus"
                '_' -> "left-shift minus"
                '=' -> "equal"
                '+' -> "left-shift equal"
                '.' -> "period"
                '>' -> "left-shift period"
                ',' -> "comma"
                '<' -> "left-shift comma"
                ';' -> "semicolon"
                ':' -> "left-shift semicolon"
                '\'' -> "quote"
                '[' -> "lbracket"
                ']' -> "rbracket"
                '{' -> "left-shift lbracket"
                '}' -> "left-shift rbracket"
                '\\' -> "backslash"
                '/' -> "slash"
                '?' -> "left-shift slash"

                else -> char.toString()
            }
        }
    }

}