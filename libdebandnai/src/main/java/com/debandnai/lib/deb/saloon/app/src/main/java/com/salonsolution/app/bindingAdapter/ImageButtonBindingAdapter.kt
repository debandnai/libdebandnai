package com.salonsolution.app.bindingAdapter

import android.widget.ImageButton
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter

@BindingAdapter("setTint")
fun ImageButton.setImageTint(@ColorInt color: Int) {
    setColorFilter(color)
}