package com.example.hidsend

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService


const val MSG_SEND_MESSAGE = 1
const val MSG_RECIVED= 2

class MainActivity : Activity(), Handler.Callback {

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
    }

    val TAG = "HID_SEND_MAIN_ACTIVITY"

    var mBound = false
    var mServiceMessenger: Messenger? = null
    var mReplyMessenger = Messenger(Handler(Looper.getMainLooper(), this))

    private val rootConnection = object : ServiceConnection {
        val TAG = "HID_SEND_ROOT_CONN"

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mServiceMessenger  = Messenger(service)
            mBound = true
            Log.d(TAG, "Service Connected")
        }

        override fun onServiceDisconnected(p0: ComponentName) {
            mServiceMessenger = null
            mBound = false
            Log.d(TAG, "Service Disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextInput =  findViewById<TextInputEditText>(R.id.editTextInput)

        val buttonSend = findViewById<MaterialButton>(R.id.buttonSend)

        val switchSensitive = findViewById<SwitchMaterial>(R.id.switchSensitive)

        switchSensitive.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                editTextInput.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                editTextInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        buttonSend.setOnClickListener {
            if(editTextInput.text.toString() != ""){
                if(mBound) {
                    val msg = Message.obtain(null, MSG_SEND_MESSAGE)
                    val bundle = Bundle()
                    bundle.putString("msg", editTextInput.text.toString())
                    msg.data = bundle
                    msg?.replyTo = mReplyMessenger
                    try {
                        mServiceMessenger?.send(msg)
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (!mBound) {
            Intent(this@MainActivity, MyRootService::class.java).also { intent ->
                Log.d(TAG, "Lets bind")
                    RootService.bind(intent, rootConnection)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if(mBound) {
            RootService.unbind(rootConnection)
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        return when(msg.what){
            MSG_RECIVED -> {
                Toast.makeText(this@MainActivity, "Reply: ${msg.data["msg"]}", Toast.LENGTH_SHORT)
                Log.d(TAG, "Reply: \"${msg.data["msg"]}\", ReturnValue: ${msg.data["returnValue"]}")
                true
            }
            else ->
                false
        }
    }

}



