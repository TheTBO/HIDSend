package com.example.hidsend


import android.content.Intent
import android.os.*
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.tabs.TabLayout
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService


const val MSG_SEND_MESSAGE = 1
const val MSG_RECEIVED = 2
const val MSG_SEND_RAW_COMMANDS = 3
const val TAG = "HID_SEND_MAIN_ACTIVITY"

class MainActivity : AppCompatActivity(), Handler.Callback {

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
    }



    private val rootConnection = RootConnection()
    private val replyMessenger = Messenger(Handler(Looper.getMainLooper(),this))

    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val rootViewModel: RootViewModel by viewModels()
        rootViewModel.setRootConnection(rootConnection)
        rootViewModel.setReplyMessenger(replyMessenger)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.text == "Keyboard") {
                    supportFragmentManager.commit {
                        replace<KeyboardFragment>(R.id.fragmentContainerView)
                    }
                } else if(tab?.text == "Input") {
                    supportFragmentManager.commit {
                        replace<InputFragment>(R.id.fragmentContainerView)
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }


    override fun onStart() {
        super.onStart()

        if (!rootConnection.mBound) {
            Intent(this@MainActivity, MyRootService::class.java).also { intent ->
                RootService.bind(intent, rootConnection)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if(rootConnection.mBound) {
            RootService.unbind(rootConnection)
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        return when(msg.what){
            MSG_RECEIVED -> {
                Log.d(TAG, "Reply ${msg.data["msg"]}")
                true
            }
            else -> false
        }
    }

}



