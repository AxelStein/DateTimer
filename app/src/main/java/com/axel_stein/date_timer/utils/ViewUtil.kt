package com.axel_stein.date_timer.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

fun TextInputLayout.showError(error: Boolean, msg: Int) {
    if (error) {
        setError(context.getString(msg))
        editText?.showKeyboard()
    }
    isErrorEnabled = error
}

fun TextInputEditText.setupEditor(onTextChanged: (String) -> Unit) {
    doAfterTextChanged { onTextChanged(text.toString()) }
    setOnEditorActionListener { _, actionId, _ ->
        var consumed = false
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
            consumed = true
        }
        consumed
    }
    setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) hideKeyboard()
    }
}

fun TextInputEditText.setEditorText(text: String, handleKeyboard: Boolean = true) {
    val current = this.text.toString()
    if (current != text) {
        setText(text)
        setSelection(length())
    }
    if (handleKeyboard) {
        if (text.isBlank()) {
            showKeyboard()
        } else if (!isFocused) {
            hideKeyboard()
        }
    }
}