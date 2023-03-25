package ie.healthylunch.app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import ie.healthylunch.app.R

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        window.statusBarColor = ContextCompat.getColor(this@ForgotPasswordActivity, R.color.sky_bg_2)
    }
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}