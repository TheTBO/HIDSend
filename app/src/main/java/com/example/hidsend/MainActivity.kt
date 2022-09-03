package com.example.hidsend

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService


const val MSG_SEND_MESSAGE = 1
const val MSG_RECEIVED = 2
const val MSG_SEND_RAW_COMMANDS = 3
const val TAG = "HID_SEND_MAIN_ACTIVITY"

class MainActivity : Activity(), Handler.Callback {

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
    }


    var mBound = false
    var mServiceMessenger: Messenger? = null
    private var mReplyMessenger = Messenger(Handler(Looper.getMainLooper(), this))

    private lateinit var editTextInput: TextInputEditText

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

        editTextInput =  findViewById(R.id.editTextInput)

        val buttonSend = findViewById<MaterialButton>(R.id.buttonSend)

        val switchSensitive = findViewById<SwitchMaterial>(R.id.switchSensitive)

        val switchRawCommand = findViewById<SwitchMaterial>(R.id.switchRawCommands)


        switchSensitive.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                hideText()
            } else {
                showText()
            }
        }

        buttonSend.setOnClickListener {
            if(editTextInput.text.toString() != ""){
                for(char in editTextInput.text.toString()){
                   Log.d(TAG, char.toString())
                }
                if(mBound) {
                    val msg = if (switchRawCommand.isChecked){
                        Message.obtain(null, MSG_SEND_RAW_COMMANDS)
                    } else {
                        Message.obtain(null, MSG_SEND_MESSAGE)
                    }
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

        switchRawCommand.setOnCheckedChangeListener { _, checked ->
            Log.d(TAG, editTextInput.inputType.toString())

            if(checked){
                switchSensitive.visibility = View.GONE
                if(switchSensitive.isChecked){
                    switchSensitive.isChecked = false
                    editTextInput.text?.clear()
                }
                editTextInput.setHint(R.string.commands)
                editTextInput.inputType = editTextInput.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }
            else{
                switchSensitive.visibility = View.VISIBLE
                editTextInput.setHint(R.string.text)
                editTextInput.inputType = editTextInput.inputType xor InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }

            Log.d(TAG, editTextInput.inputType.toString())
        }
    }

    private fun showText() {
        editTextInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }

    private fun hideText() {
        editTextInput.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    override fun onStart() {
        super.onStart()

        if (!mBound) {
            Intent(this@MainActivity, MyRootService::class.java).also { intent ->
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
            MSG_RECEIVED -> {
                Log.d(TAG, "Reply: \"${msg.data["msg"] as String}\"")
                true
            }
            else ->
                false
        }
    }

}



