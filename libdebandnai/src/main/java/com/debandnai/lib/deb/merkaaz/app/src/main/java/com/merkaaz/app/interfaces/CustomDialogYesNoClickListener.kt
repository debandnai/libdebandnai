package com.merkaaz.app.interfaces

import android.app.Dialog

interface CustomDialogYesNoClickListener {
    fun yesClickListener(dialog: Dialog?)
    fun noClickListener(dialog: Dialog?)
}