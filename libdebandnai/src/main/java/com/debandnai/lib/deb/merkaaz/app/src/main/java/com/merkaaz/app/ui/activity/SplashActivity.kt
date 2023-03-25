package com.merkaaz.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.merkaaz.app.databinding.ActivitySplashBinding
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var sharedPreff: SharedPreff

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MethodClass.setLocale(this,sharedPreff.getlanguage_code())
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        startActivity()


    }

    private fun startActivity() {
        lifecycleScope.launch {
            delay(2000L)
            val loginModel = sharedPreff.get_logindata()
            val token = loginModel.token
            if (token.isNullOrBlank())
                startActivity(Intent(this@SplashActivity, GetStartActivity::class.java))
            else
                startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
            finish()
        }
    }

}