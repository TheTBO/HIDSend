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




class MainActivity : AppCompatActivity(), Handler.Callback {

    init {
        if(Shell.getCachedShell() == null) {
            Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_REDIRECT_STDERR)
                    .setTimeout(10)
            )
        }
    }


    private val rootViewModel: RootViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if(rootViewModel.getRootConnection.value == null) rootViewModel.setRootConnection(RootConnection())
        if(rootViewModel.getReplyMessenger.value == null) rootViewModel.setReplyMessenger(Messenger(Handler(Looper.getMainLooper(),this)))

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

        val rootConnection = rootViewModel.getRootConnection.value

        if (rootConnection != null) if (!rootConnection.mBound) {
            Intent(this@MainActivity, MyRootService::class.java).also { intent ->
                RootService.bind(intent, rootConnection)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        val rootConnection = rootViewModel.getRootConnection.value


        if (rootConnection != null) if(rootConnection.mBound) {
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

    companion object {
        const val MSG_RECEIVED = 2
        const val TAG = "HID_SEND_MAIN_ACTIVITY"
    }

}



