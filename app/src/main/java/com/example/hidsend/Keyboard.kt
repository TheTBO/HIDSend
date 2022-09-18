package com.example.hidsend

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.view.View
import android.view.View.OnClickListener
import java.lang.Exception

class Keyboard(private val context: Context, keys: MutableList<Key>, private val rootConnection: RootConnection, private val replyToMessenger: Messenger) : OnClickListener {
        init {
                for (key in keys){
                        key.setOnClickListener(this)
                }
        }

        private var mod = false
        private var activeMods = mutableListOf<String>()

        override fun onClick(p0: View?) {
                val key: Key = p0 as Key

                if(key.isModifier) {
                        if(!mod){
                                activeMods.add(key.value)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        key.background.setTint(context.resources.getColor(R.color.key_background_pressed, context.theme) )
                                }else{
                                        key.background.setTint(context.resources.getColor(R.color.key_background_pressed) )
                                }
                                mod = true
                        } else {
                                if(activeMods.contains(key.value) and (key.value.length > 1)){
                                        activeMods.remove(key.value)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                key.background.setTint(context.resources.getColor(R.color.key_background_normal, context.theme) )
                                        }else{
                                                key.background.setTint(context.resources.getColor(R.color.key_background_normal) )
                                        }
                                        if(activeMods.isEmpty()) mod = false
                                } else {
                                        activeMods.add(key.value)
                                }
                        }
                } else {
                        if(rootConnection.mBound){
                                val message = Message.obtain(null, MyRootService.MSG_SEND_RAW_COMMANDS)
                                Bundle().also { bundle ->
                                        if(!mod) bundle.putString("msg", key.value)
                                        else bundle.putString("msg", "${activeMods.joinToString("")} ${key.value}")
                                        message.data = bundle
                                }
                                message.replyTo = replyToMessenger
                                try {
                                        rootConnection.mServiceMessenger?.send(message)
                                } catch (e: Exception) {
                                        e.printStackTrace()
                                }
                        }
                }
        }

}