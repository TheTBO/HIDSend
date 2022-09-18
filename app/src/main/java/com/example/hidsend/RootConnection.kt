package com.example.hidsend

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Messenger
import android.util.Log

class RootConnection : ServiceConnection {

    var mServiceMessenger: Messenger? = null
    var mBound: Boolean = false

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        mServiceMessenger  = Messenger(service)
        mBound = true
        Log.d(Companion.TAG, "Service Connected")
    }

    override fun onServiceDisconnected(p0: ComponentName) {
        mServiceMessenger = null
        mBound = false
        Log.d(Companion.TAG, "Service Disconnected")
    }

    companion object {
        const val TAG = "HID_SEND_ROOT_CONN"
    }
}