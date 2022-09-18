package com.example.hidsend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KeyboardViewModel : ViewModel() {
    private val keyboardState = MutableLiveData<Int>()
    val getKeyboardState : LiveData<Int> get() = keyboardState

    fun setKeyboardState(state: Int){
        keyboardState.value = state
        Log.d(TAG,"new state: $state")
    }

    companion object {
        const val TAG = "HID_SEND_KB_VM"
    }
}