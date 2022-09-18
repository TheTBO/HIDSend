package com.example.hidsend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.iterator
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [KeyboardSymbolsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KeyboardSymbolsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val  keyboardViewModel : KeyboardViewModel by viewModels({requireParentFragment()})

    private val rootViewModel : RootViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keyboard_symbols, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonSwitchToLetters = view.findViewById<MaterialButton>(R.id.buttonSwitchToLetters)

        buttonSwitchToLetters.setOnClickListener{
            keyboardViewModel.setKeyboardState(KeyboardFragment.KEYBOARD_STATE_LETTERS)
        }

        val group = view as ViewGroup

        val keys: MutableList<Key> = mutableListOf<Key>( )


        for(rowView in group){
            val row = rowView as ViewGroup
            for(item in row){
                Log.d(KeyboardLettersFragment.TAG, item.javaClass.toString())

                if(item.javaClass == Key::class.java)
                    (item as Key).also(keys::add)
            }
        }

        rootViewModel.getRootConnection.value?.let { rootConnection ->
            rootViewModel.getReplyMessenger.value?.let { replyToMessenger ->
                Keyboard(
                    requireContext(),
                    keys,
                    rootConnection,
                    replyToMessenger
                )
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment keyboardSymbolsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            KeyboardSymbolsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}