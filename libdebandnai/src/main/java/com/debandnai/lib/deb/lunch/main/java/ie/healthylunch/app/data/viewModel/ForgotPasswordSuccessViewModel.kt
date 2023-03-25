package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import ie.healthylunch.app.data.repository.ForgotPasswordRepository
import ie.healthylunch.app.data.repository.ParentRegistrationRepository
import ie.healthylunch.app.ui.LoginActivity
import java.lang.Exception

class ForgotPasswordSuccessViewModel(private val repository: ForgotPasswordRepository) : ViewModel() {
    fun login(activity: Activity) {
        activity.startActivity(Intent(activity, LoginActivity::class.java))
        try {
            LoginActivity.loginActivity.finish()
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }
}