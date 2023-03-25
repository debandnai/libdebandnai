package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Dialog

interface NextNewOrderListener {
    fun confirmClick(dialog: Dialog, activity: Activity)
    fun newOrderHereClick(dialog: Dialog, activity: Activity)
}