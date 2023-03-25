package com.movie.myapplication.adapter

import android.graphics.drawable.Animatable
import androidx.databinding.BindingAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo


object BindingAdapter {

    @JvmStatic
    @BindingAdapter("app:imageUrl")
    fun loadImage(draweeView: SimpleDraweeView?, url: String?) {
        val controllerBuilder = Fresco.newDraweeControllerBuilder()
        controllerBuilder.setUri("https://image.tmdb.org/t/p/original$url")
        controllerBuilder.oldController = draweeView?.controller
        controllerBuilder.controllerListener = object : BaseControllerListener<ImageInfo>() {
            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                super.onFinalImageSet(id, imageInfo, animatable)
            }
        }
        draweeView?.controller = controllerBuilder.build()
    }
}