package com.example.hidsend

import android.content.Intent
import android.os.*
import android.util.Log
import com.topjohnwu.superuser.ipc.RootService


class MyRootService : RootService(), Handler.Callback {

    companion object {
        init {
            System.loadLibrary("hidsend")
        }
    }

    private external fun send_message(msg: String): Int


    private lateinit var mMessenger: Messenger


    override fun onBind(intent: Intent): IBinder {
        val handler = Handler(Looper.getMainLooper(), this)
        mMessenger = Messenger(handler)
        return mMessenger.binder
    }

    override fun handleMessage(msg: Message): Boolean {
        return when (msg.what) {
            MSG_SEND_MESSAGE -> {
                val replyMessage = Message.obtain(null, MSG_RECIVED)
                Bundle().also { bundle ->
                    bundle.putString("msg", "Received: ${msg.data["msg"]}")
                    bundle.putInt("returnValue", send_message(msg.data["msg"] as String))
                    replyMessage.data = bundle
                }
                msg.replyTo.send(replyMessage)
                true
            }
            else -> false
        }

    }

}