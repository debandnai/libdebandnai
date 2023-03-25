package com.salonsolution.app.interfaces

import android.widget.ImageView

interface ImageSliderClickListener {
    fun onItemClick(position: Int, link: String?, imageView: ImageView)
}