package ie.healthylunch.app.data

import android.content.Intent
import android.util.Log
import android.view.View
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.LoginRegistrationActivity

class CommonClass() {

    fun homeButtonClick(view:View) {
        view.context.startActivity(Intent(view.context, DashBoardActivity::class.java))
    }
}