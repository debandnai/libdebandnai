package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Dialog

interface DialogOrderCancelListener {
    fun orderOnClick(dialog: Dialog, activity: Activity, dayName: String)
    fun cancelOnClick(dialog: Dialog, activity: Activity)
}