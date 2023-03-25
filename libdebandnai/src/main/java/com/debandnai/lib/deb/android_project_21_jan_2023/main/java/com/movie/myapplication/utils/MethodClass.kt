package com.movie.myapplication.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.movie.myapplication.R

object MethodClass {
    fun custom_loader(ctx: Context?, msg: String?): Dialog? {
        var dialog: Dialog? = null
        ctx?.let {
            dialog = Dialog(it)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog?.setCancelable(false)
            dialog?.setContentView(R.layout.custom_loader)

            val text = dialog?.findViewById<View>(R.id.text_dialog) as TextView
            text.text = msg
        }
        return dialog



    }
}