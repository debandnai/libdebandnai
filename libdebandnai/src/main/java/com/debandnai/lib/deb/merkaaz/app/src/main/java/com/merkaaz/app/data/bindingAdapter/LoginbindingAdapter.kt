package com.merkaaz.app.data.bindingAdapter

import android.app.Activity
import android.graphics.drawable.Animatable

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.merkaaz.app.adapter.LoginSpinnerAdapter
import com.merkaaz.app.data.model.CountryListModel

object LoginbindingAdapter {
    @JvmStatic
    @BindingAdapter("spinnerAdapter")
    fun setAppCompatSpinner(view: Spinner,adapterList : ArrayList<CountryListModel>) {
        val loginSpinnerAdapter =  LoginSpinnerAdapter(view.context as Activity,adapterList)
        view.adapter = loginSpinnerAdapter
    }
    @JvmStatic
    @BindingAdapter("app:imageUrl")
    fun loadImage(draweeView: SimpleDraweeView?, url: String?) {
        val controllerBuilder = Fresco.newDraweeControllerBuilder()
        controllerBuilder.setUri(url)
        controllerBuilder.oldController = draweeView?.controller
        controllerBuilder.controllerListener = object : BaseControllerListener<ImageInfo>() {
            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                super.onFinalImageSet(id, imageInfo, animatable)
                //image is loaded
            }
        }
        draweeView?.controller = controllerBuilder.build()
        //draweeView.hierarchy.setPlaceholderImage(ContextCompat.getDrawable(draweeView.context, R.drawable.no_image))
        //draweeView.hierarchy.setFailureImage(ContextCompat.getDrawable(draweeView.context, R.drawable.no_image))

    }
}