package com.example.hidsend

import android.os.Bundle
import android.os.Message
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import java.lang.Exception


class KeyboardFragment : Fragment(){

    private val rootViewModel: RootViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransitionInflater.from(requireContext()).also { transitionInflater ->
            enterTransition = transitionInflater.inflateTransition(R.transition.slide_right)
            exitTransition = transitionInflater.inflateTransition(R.transition.fade)
        }
    }

    private val keyOnClickListener = View.OnClickListener {
        val key: Key = it as Key
        val rootConnection = rootViewModel.getRootConnection.value
        val replyToMessenger = rootViewModel.getReplyMessenger.value

        if(rootConnection?.mBound == true){
            if(!key.isModifier) {
                val message = Message.obtain(null, MSG_SEND_RAW_COMMANDS)
                Bundle().also { bundle ->
                    bundle.putString("msg", key.value)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keyboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val keyA = view.findViewById<Key>(R.id.keyA)
        val keyBackspace = view.findViewById<Key>(R.id.keyBackspace)

        keyBackspace.setOnClickListener(keyOnClickListener)
        keyA.setOnClickListener(keyOnClickListener)
    }

}