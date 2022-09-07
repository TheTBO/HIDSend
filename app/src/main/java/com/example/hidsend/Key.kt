package com.example.hidsend

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton


class Key(context: Context, attrs: AttributeSet) : MaterialButton(context, attrs) {
    var isModifier: Boolean = false
    var value: String = ""


    init {
        context.theme.obtainStyledAttributes(
        attrs,
        R.styleable.Key,
            0, 0).apply {
               try {
                   isModifier = getBoolean(R.styleable.Key_isModifier, false)
                   value = getString(R.styleable.Key_value).toString()
               } finally {
                   recycle()
               }
        }
    }

}