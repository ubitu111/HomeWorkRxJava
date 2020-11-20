package ru.focusstart.kireev.homeworkrxjava.util

import android.text.Editable
import android.text.TextWatcher

class MyTextWatcher(afterChangedFunc: (text: Editable?) -> Unit) : TextWatcher {
    private var function : ((text: Editable?) -> Unit)? = afterChangedFunc

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        function?.invoke(s)
    }
}