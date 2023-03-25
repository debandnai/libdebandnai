package com.merkaaz.app.ui.activity

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.merkaaz.app.R
import com.merkaaz.app.databinding.ActivityOrderPlacedBinding
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderPlacedActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderPlacedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_placed)
        binding.lifecycleOwner = this
        setData()
        viewOnClick()
    }

    private fun viewOnClick() {
        binding.btnViewOrder.setOnClickListener {
            startActivity(
                Intent(this, MyOrderActivity::class.java)

                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        binding.btnContinueShopping.setOnClickListener {
            startActivity(
                Intent(this, DashBoardActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }


    }

    private fun setData() {
//Animation.pauseAnimation;
        binding.lottieConfettiAnim.setAnimationFromUrl(Constants.LOTTIE_CONFETTIE_LINK)
        binding.lottieConfettiAnim.playAnimation()
        binding.lottieConfettiAnim.addAnimatorListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {

                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
    }

    override fun onBackPressed() {
        // super.onBackPressed()
    }
}