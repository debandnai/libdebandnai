package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Dialog
import android.widget.TextView

interface DialogEditEmailListener {
    fun saveOnClick(
        dialog: Dialog,
        activity: Activity,
        oldEmail: String,
        newEmail: String,
        errorTv: TextView
    )
}