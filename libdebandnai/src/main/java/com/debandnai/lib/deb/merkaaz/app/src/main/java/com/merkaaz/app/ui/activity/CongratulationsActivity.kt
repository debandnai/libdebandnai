package com.merkaaz.app.ui.activity

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.merkaaz.app.R
import com.merkaaz.app.databinding.ActivityCongratulationsBinding

import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.PAGE_TYPE

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CongratulationsActivity : BaseActivity(){
    private lateinit var binding: ActivityCongratulationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =DataBindingUtil.setContentView(this, R.layout.activity_congratulations)

        setdata()

    }

    private fun setdata() {
        binding.lottieTickAnim.setAnimationFromUrl(Constants.LOTTIE_TICK_LINK)
        binding.lottieTickAnim.playAnimation()
        binding.lottieConfettiAnim.setAnimationFromUrl(Constants.LOTTIE_CONFETTIE_LINK)
        //        lottie_anim.setSpeed(0.7f);
        binding.lottieConfettiAnim.playAnimation()
        binding.lottieConfettiAnim.addAnimatorListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    lifecycleScope.launch {
                        delay(500L)
                        startActivity(Intent(this@CongratulationsActivity, DashBoardActivity::class.java))
                       finish()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
    }


}