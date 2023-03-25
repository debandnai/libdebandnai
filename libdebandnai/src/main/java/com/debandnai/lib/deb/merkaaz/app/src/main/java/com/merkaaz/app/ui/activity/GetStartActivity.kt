package com.merkaaz.app.ui.activity

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.merkaaz.app.R
import com.merkaaz.app.databinding.ActivityGetStartBinding
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.MethodClass.hideSoftKeyboard
import com.merkaaz.app.utils.SharedPreff
import javax.inject.Inject


class GetStartActivity : BaseActivity() {
    private lateinit var binding: ActivityGetStartBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setLocale("pt")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_start)

        initialise()
        onViewClick()


    }

    private fun initialise() {
        hideSoftKeyboard(this)
        //show language bottom sheet
        if (sharedPreff.getlanguage_id().equals("-1"))
            MethodClass.showBottomSheetDialog(this, "get started")


        val imageRequest =
            ImageRequestBuilder.newBuilderWithResourceId(R.drawable.landing_page_background).build()

        val controllerBuilder = Fresco.newDraweeControllerBuilder()
        controllerBuilder.setUri(imageRequest.sourceUri)
        controllerBuilder.oldController = binding.sdvImage.controller
        controllerBuilder.controllerListener = object : BaseControllerListener<ImageInfo>() {
            override fun onFinalImageSet(
                id: String?,
                imageInfo: ImageInfo?,
                animatable: Animatable?
            ) {
                super.onFinalImageSet(id, imageInfo, animatable)
                //image is loaded
            }
        }
        binding.sdvImage.controller = controllerBuilder.build()
    }

    private fun onViewClick() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

}