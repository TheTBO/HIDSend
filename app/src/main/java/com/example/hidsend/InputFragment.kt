package com.example.hidsend

import android.os.Bundle
import android.os.Message
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.topjohnwu.superuser.ipc.RootService


class InputFragment : Fragment() {

    private val rootViewModel: RootViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransitionInflater.from(requireContext()).also { transitionInflater ->
            enterTransition = transitionInflater.inflateTransition(R.transition.slide_left)
            exitTransition = transitionInflater.inflateTransition(R.transition.fade)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextInput = view.findViewById<EditText>(R.id.editTextInput)

        val buttonSend = view.findViewById<MaterialButton>(R.id.buttonSend)

        val switchSensitive = view.findViewById<SwitchMaterial>(R.id.switchSensitive)

        val switchRawCommand = view.findViewById<SwitchMaterial>(R.id.switchRawCommands)

        switchSensitive.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                hideText(editTextInput)
            } else {
                showText(editTextInput)
            }
        }

        editTextInput.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                buttonSend.performClick()
                true
            }else {
                false
            }
        }


        buttonSend.setOnClickListener {

            val rootConnection = rootViewModel.getRootConnection.value
            val replyToMessenger = rootViewModel.getReplyMessenger.value

            if (editTextInput.text.toString() != "") {

                Log.d(TAG, editTextInput.text.toString())

                if(rootConnection?.mBound == true) {

                    val msg = if (switchRawCommand.isChecked) {
                        Message.obtain(null, MyRootService.MSG_SEND_RAW_COMMANDS)
                    } else {
                        Message.obtain(null, MyRootService.MSG_SEND_MESSAGE)
                    }
                    Bundle().also { bundle ->
                        bundle.putString("msg", editTextInput.text.toString())
                        msg.data = bundle
                     }
                    msg.replyTo = replyToMessenger
                    try {
                        rootConnection.mServiceMessenger?.send(msg)
                    } catch (e: Exception) {
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
        }
    }

    private fun showText(editText: EditText) {
        editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }

    private fun hideText(editText: EditText) {
        editText.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    companion object {
        const val TAG = "HID_SEND_INPUT_FRAG"
    }

}