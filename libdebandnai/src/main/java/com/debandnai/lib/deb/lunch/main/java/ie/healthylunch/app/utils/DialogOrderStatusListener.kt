package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Dialog

interface DialogOrderStatusListener {
    fun okOnClick(dialog: Dialog, pageRedirection: Int)
}