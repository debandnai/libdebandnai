package com.salonsolution.app.bindingAdapter

import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("requestFocus")
fun requestFocus(view: Button, isFocus:Boolean){
    if(isFocus){
        view.requestFocus()
    }
}