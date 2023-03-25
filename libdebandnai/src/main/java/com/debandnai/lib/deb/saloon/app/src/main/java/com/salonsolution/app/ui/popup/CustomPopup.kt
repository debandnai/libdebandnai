package com.salonsolution.app.ui.popup

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.salonsolution.app.R
import com.salonsolution.app.interfaces.DialogButtonClick
import com.salonsolution.app.interfaces.PositiveNegativeCallBack

object CustomPopup {

    fun showMessageDialogYesNo(
        mContext: Context?,
        title: String? = null,
        description: String?,
        positiveNegativeCallBack: PositiveNegativeCallBack,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        isCancelable: Boolean = true
    ) {
        mContext?.let {
            val dialog = Dialog(it)
            dialog.setTitle(title)
            dialog.setCancelable(isCancelable)
            dialog.setContentView(R.layout.dialog_yes_no_button)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvDescription = dialog.findViewById<TextView>(R.id.tvDescription)
            tvDescription.text = description
            val btPositive = dialog.findViewById<TextView>(R.id.btPositive)
            val btNegative = dialog.findViewById<TextView>(R.id.btNegative)
            title?.let { dec ->
                tvTitle.text = dec
            }
            positiveButtonText?.let { dec ->
                btPositive.text = dec
            }
            negativeButtonText?.let { dec ->
                btNegative.text = dec
            }
            btPositive.setOnClickListener {
                dialog.dismiss()
                positiveNegativeCallBack.onPositiveButtonClick()
            }
            btNegative.setOnClickListener {
                dialog.dismiss()
                positiveNegativeCallBack.onNegativeButtonClick()
            }
            dialog.show()
        }

    }

    fun showPopupMessageButtonClickDialog(
        mContext: Context?,
        title: String?,
        desc: String?,
        buttonText: String?,
        isCancelable: Boolean = false,
        dialogButtonClick: DialogButtonClick
    ) {
        try {
            val dialog = Dialog(mContext!!)
            dialog.setTitle(title)
            dialog.setCancelable(isCancelable)
            dialog.setContentView(R.layout.error_dialog)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            val popupTitle = dialog.findViewById<TextView>(R.id.popupTitle)
            val tvError = dialog.findViewById<TextView>(R.id.tvError)
            title?.let { dec ->
                popupTitle.text = dec
            }
            tvError.text = desc
            val report = dialog.findViewById<TextView>(R.id.report)
            buttonText?.let { report.text = it }
            report.setOnClickListener {
                dialog.dismiss()
                dialogButtonClick.dialogButtonCallBack(dialog)
            }
            // final DecimalFormat df = new DecimalFormat("0.00");
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showSuccessfullyAddedToCartPopup(
        mContext: Context?,
        title: String? = null,
        description: String?,
        positiveNegativeCallBack: PositiveNegativeCallBack,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        isCancelable: Boolean = false
    ) {
        mContext?.let {
            val dialog = Dialog(it)
            dialog.setTitle(title)
            dialog.setCancelable(isCancelable)
            dialog.setContentView(R.layout.successfully_added_dialog)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvDescription = dialog.findViewById<TextView>(R.id.tvDescription)
            tvDescription.text = description
            val btPositive = dialog.findViewById<TextView>(R.id.btPositive)
            val btNegative = dialog.findViewById<TextView>(R.id.btNegative)
            title?.let { dec ->
                tvTitle.text = dec
            }
            positiveButtonText?.let { dec ->
                btPositive.text = dec
            }
            negativeButtonText?.let { dec ->
                btNegative.text = dec
            }
            btPositive.setOnClickListener {
                dialog.dismiss()
                positiveNegativeCallBack.onPositiveButtonClick()
            }
            btNegative.setOnClickListener {
                dialog.dismiss()
                positiveNegativeCallBack.onNegativeButtonClick()
            }
            dialog.show()
        }

    }

    fun loadingPopup(context: Context?): Dialog? {
        context?.let {
            val dialog = Dialog(context)
            dialog.setCancelable(false)
            dialog.window
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.custom_progress_bar)
            return dialog
        }
        return null

    }

    fun showCancelOrderPopup(
        mContext: Context?,
        positiveNegativeCallBack: PositiveNegativeCallBack,
        isCancelable: Boolean = false
    ) {
        mContext?.let {
            val dialog = Dialog(it)
            dialog.setTitle("Salon")
            dialog.setCancelable(isCancelable)
            dialog.setContentView(R.layout.cancel_order_dialog)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )

            val btPositive = dialog.findViewById<TextView>(R.id.btConfirm)
            val btNegative = dialog.findViewById<TextView>(R.id.btDoNotCancel)
            btPositive.setOnClickListener {
                dialog.dismiss()
                positiveNegativeCallBack.onPositiveButtonClick()
            }
            btNegative.setOnClickListener {
                dialog.dismiss()
                positiveNegativeCallBack.onNegativeButtonClick()
            }
            dialog.show()
        }

    }


}