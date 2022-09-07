package com.example.hidsend

import android.os.Messenger
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RootViewModel : ViewModel() {
    private val mutableRootConnection = MutableLiveData<RootConnection>()
    val getRootConnection: LiveData<RootConnection> get() = mutableRootConnection

    private val mutableReplyMessenger = MutableLiveData<Messenger>()
    val getReplyMessenger : LiveData<Messenger> get() = mutableReplyMessenger

    fun setReplyMessenger(replyMessenger: Messenger){
        replyMessenger.also { mutableReplyMessenger.value = it }
    }

    fun setRootConnection(rootConnection: RootConnection){
        rootConnection.also { mutableRootConnection.value = it }
    }
}