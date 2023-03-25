package com.salonsolution.app.bindingAdapter

import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.chaos.view.PinView
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.UtilsCommon

@BindingAdapter("requestFocus")
fun requestFocus(view: EditText, isFocus:Boolean){
    if(isFocus){
        view.requestFocus()
    }
}

@BindingAdapter("requestFocus")
fun requestFocus(view: PinView, isFocus:Boolean){
    if(isFocus){
        view.requestFocus()
    }
}
@BindingAdapter("hideKeyBoard")
fun hideKeyboard(view: PinView, value:String?){
    value?.let {
        if(it.length>= Constants.PIN_VIEW_LENGTH){
            UtilsCommon.hideKeyboard(view)
        }
    }
}
