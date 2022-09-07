package com.example.hidsend

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Messenger
import android.util.Log

class RootConnection : ServiceConnection {
    private val mTAG = "HID_SEND_ROOT_CONN"

    var mServiceMessenger: Messenger? = null
    var mBound: Boolean = false

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        mServiceMessenger  = Messenger(service)
        mBound = true
        Log.d(mTAG, "Service Connected")
    }

    override fun onServiceDisconnected(p0: ComponentName) {
        mServiceMessenger = null
        mBound = false
        Log.d(mTAG, "Service Disconnected")
    }
}