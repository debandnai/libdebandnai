package ie.healthylunch.app.utils

import android.text.Editable
import android.view.View

interface TextWatcherListener
    {
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int,view: View)
        fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int,view: View)
        fun afterTextChanged(s: Editable?,view: View)
    }