package ie.healthylunch.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import ie.healthylunch.app.R
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.CHECK_REGISTRATION
import ie.healthylunch.app.utils.Constants.Companion.EDIT_ALLERGEN
import ie.healthylunch.app.utils.Constants.Companion.FOR
import ie.healthylunch.app.utils.Constants.Companion.HAS_ALLERGEN__POPUP_SHOW_TAG
import ie.healthylunch.app.utils.Constants.Companion.PAGE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_MINUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID_TAG
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_NAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE_TAG
import ie.healthylunch.app.utils.Constants.Companion.YES
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        window.statusBarColor = ContextCompat.getColor(this@RegistrationActivity, R.color.sky_bg_2)
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        if (Objects.equals(intent.getStringExtra("for"), "email_verify")) {
            val bundle = Bundle()
            bundle.putString("email", intent.getStringExtra("email"))
            Constants.CHECK_REGISTRATION = true
            navController.navigate(R.id.registrationOtpFragment, bundle)
        } else if (Objects.equals(intent.getStringExtra("for"), "add_student")) {
            navController.navigate(R.id.addStudentNewStepOneFragment)
        }
        else if (intent.getStringExtra(FOR) == EDIT_ALLERGEN){
            val bundle = Bundle()
            bundle.putInt(STUDENT_ID_TAG, intent.getIntExtra(STUDENT_ID_TAG,STATUS_ZERO))
            bundle.putString(USER_TYPE_TAG, intent.getStringExtra(USER_TYPE_TAG))
            bundle.putString(STUDENT_NAME_TAG, intent.getStringExtra(STUDENT_NAME_TAG))
            bundle.putString(PAGE, EDIT_ALLERGEN)

            if (intent.getStringExtra(HAS_ALLERGEN__POPUP_SHOW_TAG) == YES){
                navController.navigate(R.id.checkHasAllergenFragment, bundle)
            } else{
                navController.navigate(R.id.allergenFragment, bundle)
            }

        }
    }


    override fun onResume() {
        super.onResume()
        //clear all push notifications
        NotificationManagerCompat.from(this).cancelAll()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var registrationActivity: Activity
        var emailForOtp: String = ""
    }

    init {
        registrationActivity = this
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        /*val isLogin: IsLogin? = UserPreferences.getAsObject<IsLogin>(
            this,
            LOGIN_CHECK
        )
        if (isLogin?.isLogin == 1){
            finish()
        }*/
    }
}