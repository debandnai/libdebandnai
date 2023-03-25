package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Dialog

interface DialogYesNoListener {
    fun yesOnClick(dialog: Dialog, activity: Activity)
    fun noOnClick(dialog: Dialog, activity: Activity)

}