package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Dialog

interface DialogOkListener {
    fun okOnClick(dialog: Dialog, activity: Activity)
}