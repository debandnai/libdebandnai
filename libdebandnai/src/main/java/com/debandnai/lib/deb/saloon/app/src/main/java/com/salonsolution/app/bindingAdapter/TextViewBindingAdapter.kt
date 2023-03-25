package com.salonsolution.app.bindingAdapter

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.salonsolution.app.utils.UtilsCommon

@BindingAdapter("requestFocus")
fun requestFocus(view: TextView, isFocus: Boolean) {
    if (isFocus) {
        view.requestFocus()
        UtilsCommon.hideKeyboard(view)
    }
}

@BindingAdapter(
    value = ["endDrawable", "isShowDrawable"],
    requireAll = false
)
fun setEndDrawable(view: TextView, endDrawable: Drawable?, isShowDrawable: Boolean = true) {
    if (isShowDrawable)
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, endDrawable, null)
    else
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
}