package com.example.hidsend

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer


class KeyboardFragment : Fragment(){



    val keyboardViewModel : KeyboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransitionInflater.from(requireContext()).also { transitionInflater ->
            enterTransition = transitionInflater.inflateTransition(R.transition.slide_right)
            exitTransition = transitionInflater.inflateTransition(R.transition.fade)
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

        keyboardViewModel.setKeyboardState(KEYBOARD_STATE_LETTERS)

        childFragmentManager.commit{
            replace<KeyboardLettersFragment>(R.id.keyboardFragmentView)
        }
        keyboardViewModel.getKeyboardState.observe(viewLifecycleOwner, Observer { state ->
            if(state != null) {
                when(state){
                    KEYBOARD_STATE_LETTERS ->
                        childFragmentManager.commit{
                            replace<KeyboardLettersFragment>(R.id.keyboardFragmentView)
                        }
                    KEYBOARD_STATE_SYMBOLS ->
                        childFragmentManager.commit{
                            replace<KeyboardSymbolsFragment>(R.id.keyboardFragmentView)
                        }
                }
            }
        })


    }


    companion object {
        const val KEYBOARD_STATE_LETTERS = 0
        const val KEYBOARD_STATE_SYMBOLS = 1
    }


}