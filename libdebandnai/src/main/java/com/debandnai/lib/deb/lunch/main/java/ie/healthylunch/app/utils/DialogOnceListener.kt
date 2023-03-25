package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Dialog

interface DialogOnceListener {
    fun onceClick(dialog: Dialog, activity: Activity)
}