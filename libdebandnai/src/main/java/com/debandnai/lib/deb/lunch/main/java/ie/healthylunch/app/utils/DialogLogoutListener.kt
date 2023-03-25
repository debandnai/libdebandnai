package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Dialog

interface DialogLogoutListener {
    fun yesOnClick(dialog: Dialog)
    fun noOnClick(dialog: Dialog)

}