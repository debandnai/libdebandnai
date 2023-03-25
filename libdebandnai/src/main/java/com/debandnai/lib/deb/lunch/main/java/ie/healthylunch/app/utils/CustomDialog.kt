package ie.healthylunch.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import ie.healthylunch.app.R
import ie.healthylunch.app.databinding.*


class CustomDialog {
    companion object {
        //Yes no type dialog
        fun showYesNoTypeDialog(
            activity: Activity,
            contentMsg: String,
            dialogYesNoListener: DialogYesNoListener
        ) {
            if (!activity.isFinishing) {
                val dialog = Dialog(activity)
                dialog.window?.setBackgroundDrawableResource(R.color.transparent)
                val binding: CustomYesNoPopupBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(activity),
                    R.layout.custom_yes_no_popup,
                    null,
                    false
                )
                dialog.setContentView(binding.root)
                dialog.setCancelable(false)


                binding.contentTv.text = contentMsg

                binding.yesTv.setOnClickListener {

                    dialogYesNoListener.yesOnClick(dialog, activity)
                }

                binding.noTv.setOnClickListener {
                    dialogYesNoListener.noOnClick(dialog, activity)
                }
                dialog.show()
            }
        }

        fun exitAppDialog(
            context: Context,
            dialogYesNoListener: DialogYesNoListener
        ){
            val exitAppDialog = Dialog(context)

            exitAppDialog.window?.let { window ->
                window.setBackgroundDrawableResource(R.color.transparent)
                window.decorView.setBackgroundResource(android.R.color.transparent)
                window.setDimAmount(0.0f)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val dialogBinding =
                ExitAppDialogBinding.inflate(LayoutInflater.from(context))
            exitAppDialog.setContentView(dialogBinding.root)
            exitAppDialog.setCancelable(false)

            dialogBinding.btnYes.setOnClickListener {
                dialogYesNoListener.yesOnClick(exitAppDialog, context as Activity)
            }
            dialogBinding.btnNo.setOnClickListener {
                dialogYesNoListener.noOnClick(exitAppDialog, context as Activity)
            }
            exitAppDialog.show()
        }

        fun logoutDialog(
            activity: Activity,
            contentMsg: String,
            dialogLogoutListener: DialogLogoutListener
            ) {
            if (!activity.isFinishing) {
                val dialog = Dialog(activity)
                dialog.window?.setBackgroundDrawableResource(R.color.transparent)
                val binding: CustomYesNoPopupBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(activity),
                    R.layout.custom_yes_no_popup,
                    null,
                    false
                )
                dialog.setContentView(binding.root)
                dialog.setCancelable(false)


                binding.contentTv.text = contentMsg

                binding.yesTv.setOnClickListener {

                    dialogLogoutListener.yesOnClick(dialog)
                }

                binding.noTv.setOnClickListener {
                    dialogLogoutListener.noOnClick(dialog)
                }
                dialog.show()
            }
        }

        //no favorite product in the list
        fun noFavoritesInfoDialog(
            context: Context,
            dialogOkListener: DialogOkListener
        ){
            val dialog = Dialog(context)
            dialog.window?.let { window ->
                window.setBackgroundDrawableResource(R.color.transparent)
                window.decorView.setBackgroundResource(R.color.transparent)
                window.setDimAmount(0.0f)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val dialogBinding = NoFavoritesInfoDialogBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)

            dialogBinding.btnOk.setOnClickListener {
                dialogOkListener.okOnClick(dialog, context as Activity)
            }
            dialog.show()
        }

        //Ok type dialog
        fun showOkTypeDialog(
            activity: Activity,
            contentMsg: String,
            dialogOkListener: DialogOkListener
        ) {
            if (!activity.isFinishing) {
                val dialog = Dialog(activity)
                dialog.window?.setBackgroundDrawableResource(R.color.transparent)
                val binding: InfoLayoutWithMsgBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(activity),
                    R.layout.info_layout_with_msg,
                    null,
                    false
                )
                dialog.setContentView(binding.root)
                dialog.setCancelable(false)

                binding.contTv.text = contentMsg
                binding.okTv.setTextColor(
                    ContextCompat.getColor(
                        binding.okTv.context,
                        R.color.blue
                    )
                )
                binding.okTv.setOnClickListener {
                    dialogOkListener.okOnClick(dialog, activity)

                }
                dialog.show()
            }
        }

        //Order Status dialog
        /*fun orderStatusDialog(
            activity: Activity,
            contentMsg1: String,
            contentMsg2: String,
            pageRedirection: Int,
            dialogOrderStatusListener:DialogOrderStatusListener
        ) {
            if (!activity.isFinishing) {
                val dialog = Dialog(activity)
                dialog.window?.setBackgroundDrawableResource(R.color.transparent)
                dialog.window?.decorView?.setBackgroundResource(android.R.color.transparent)
                dialog.window?.setDimAmount(0.0f)
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val binding: OrderStatusLayoutWithMsgBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(activity),
                    R.layout.order_status_layout_with_msg,
                    null,
                    false,

                )
                dialog.setContentView(binding.root)
                dialog.setCancelable(false)

                binding.tvOrderStatusMessage.text = contentMsg1
                when(pageRedirection){
                    STATUS_ZERO->{
                        binding.imgOrderSuccessStatus.setBackgroundResource(R.drawable.order_plased)
                        MethodClass.rotedImage(binding.imgOrderSuccessStatus)
                        binding.btnOrderstatus.visibility=View.GONE
                        binding.tvOrderStatusMessage.visibility=View.GONE
                        binding.tvOrderStatusMessage2.visibility=View.VISIBLE
                        binding.tvTitle.text=contentMsg1
                        binding.tvOrderStatusMessage2.text=contentMsg2
                        dialogOrderStatusListener.okOnClick(dialog,pageRedirection)
                    }
                    STATUS_ONE->{
                        binding.imgOrderSuccessStatus.setBackgroundResource(R.drawable.ic_green_tick_2)
                        binding.tvOrderStatusMessage2.visibility=View.GONE
                        binding.btnOrderstatus.visibility=View.VISIBLE
                    }
                    STATUS_TWO->{
                        binding.imgOrderSuccessStatus.setBackgroundResource(R.drawable.order_plased)
                        MethodClass.rotedImage(binding.imgOrderSuccessStatus)
                        binding.tvOrderStatusMessage2.visibility=View.VISIBLE
                        binding.btnOrderstatus.visibility=View.GONE
                        binding.tvOrderStatusMessage2.text=contentMsg2
                        android.os.Handler(Looper.getMainLooper()).postDelayed({
                            dialogOrderStatusListener.oorderStatusMessagekOnClick(dialog,pageRedirection)
                        }, 2000)

                    }
                    STATUS_THREE->{
                        binding.imgOrderSuccessStatus.setBackgroundResource(R.drawable.ic_warning)
                        binding.tvOrderStatusMessage2.visibility=View.GONE
                        binding.btnOrderstatus.visibility=View.VISIBLE
                    }
                }

                binding.btnOrderstatus.text = contentMsg2
                binding.btnOrderstatus.setOnClickListener {
                    dialogOrderStatusListener.okOnClick(dialog,pageRedirection)
                }
                dialog.show()
            }
        }*/

        //Order Status dialog
        /*fun orderStatusDialog(
            activity: Activity,
            status: Int,
            dialogOrderStatusListener:DialogOrderStatusListener
        ) {
            var orderStatusMessage = ""
            var orderStatusMessageButtonText = ""
            when (status) {
                STATUS_ZERO -> {
                    orderStatusMessage = activity.getString(R.string.transaction_successful)
                    orderStatusMessageButtonText = activity.getString(R.string.view_my_order)
                }
                STATUS_ONE -> {
                    orderStatusMessage = activity.getString(R.string.transaction_successful)
                    orderStatusMessageButtonText = activity.getString(R.string.view_my_order)
                }
                STATUS_TWO -> {
                    orderStatusMessage = getString(R.string.payment_under_process)
                    orderStatusMessageButtonText =
                        getString(R.string.please_don_t_go_back_or_refresh)


                }
                STATUS_THREE -> {
                    orderStatusMessage =
                        getString(R.string.Please_top_up_your_account_to_ensure_your_order_is_processed)
                    orderStatusMessageButtonText = getString(R.string.wallet)
                }

            }
            if (!activity.isFinishing) {
                val dialog = Dialog(activity)
                dialog.window?.setBackgroundDrawableResource(R.color.transparent)
                dialog.window?.decorView?.setBackgroundResource(android.R.color.transparent)
                dialog.window?.setDimAmount(0.0f)
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val binding: OrderStatusLayoutWithMsgBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(activity),
                    R.layout.order_status_layout_with_msg,
                    null,
                    false,

                    )
                dialog.setContentView(binding.root)
                dialog.setCancelable(false)


                when(status){
                    STATUS_ZERO->{
                        binding.imgOrderSuccessStatus.setBackgroundResource(R.drawable.order_plased)
                        MethodClass.rotedImage(binding.imgOrderSuccessStatus)
                        binding.btnOrderstatus.visibility=View.GONE
                        binding.tvOrderStatusMessage.visibility=View.GONE
                        binding.tvOrderStatusMessage2.visibility=View.VISIBLE
                        binding.tvTitle.text=activity.getString(R.string.transaction_successful)
                        binding.tvOrderStatusMessage.text = contentMsg1
                        binding.tvOrderStatusMessage2.text=contentMsg2
                        dialogOrderStatusListener.okOnClick(dialog,pageRedirection)
                    }
                    STATUS_ONE->{
                        binding.imgOrderSuccessStatus.setBackgroundResource(R.drawable.ic_green_tick_2)
                        binding.tvOrderStatusMessage2.visibility=View.GONE
                        binding.btnOrderstatus.visibility=View.VISIBLE
                    }
                    STATUS_TWO->{
                        binding.imgOrderSuccessStatus.setBackgroundResource(R.drawable.order_plased)
                        MethodCl



 ass.rotedImage(binding.imgOrderSuccessStatus)
                        binding.tvOrderStatusMessage2.visibility=View.VISIBLE
                        binding.btnOrderstatus.visibility=View.GONE
                        binding.tvOrderStatusMessage2.text=contentMsg2
                        android.os.Handler(Looper.getMainLooper()).postDelayed({
                            dialogOrderStatusListener.oorderStatusMessagekOnClick(dialog,pageRedirection)
                        }, 2000)

                    }
                    STATUS_THREE->{
                        binding.imgOrderSuccessStatus.setBackgroundResource(R.drawable.ic_warning)
                        binding.tvO



 rderStatusMessage2.visibility=View.GONE
                        binding.btnOrderstatus.visibility=View.VISIBLE
                    }
                }

                binding.btnOrderstatus.text = contentMsg2
                binding.btnOrderstatus.setOnClickListener {
                    dialogOrderStatusListener.okOnClick(dialog,pageRedirection)
                }
                dialog.show()
            }
        }
*/


        //Ok type dialog
        fun showOkTypeDialog(
            activity: Activity,
            title: String?,
            contentMsg: String?,
            dialogOkListener: DialogOkListener
        ) {
            if (!activity.isFinishing) {
                val dialog = Dialog(activity)
                dialog.window?.setBackgroundDrawableResource(R.color.transparent)
                val binding: InfoLayoutWithMsgBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(activity),
                    R.layout.info_layout_with_msg,
                    null,
                    false
                )
                dialog.setContentView(binding.root)
                dialog.setCancelable(false)

                binding.contTv.text =
                    contentMsg ?: activity.resources.getString(R.string.no_data_found)
                binding.popupTitle.text = title ?: activity.resources.getString(R.string.app_name)
                binding.okTv.setOnClickListener {
                    dialogOkListener.okOnClick(dialog, activity)

                }
                dialog.show()
            }
        }

        //Ok type dialog
        fun showWarningTypeDialog(
            activity: Activity,
            contentMsg: String,
            dialogOkListener: DialogOkListener
        ) {
            if (!activity.isFinishing) {
                val dialog = Dialog(activity)

                with(dialog){
                    window?.let { window ->
                        window.setBackgroundDrawableResource(R.color.transparent)
                        window.decorView.setBackgroundResource(android.R.color.transparent)
                        window.setDimAmount(0.0f)
                        window.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                    val dialogBinding = FirstDayorderItemYesNoBinding.inflate(
                        LayoutInflater.from(activity)
                    )

                    setContentView(dialogBinding.root)
                    setCancelable(false)

                    dialogBinding.contentTv.text = contentMsg
                    dialogBinding.okTv.setOnClickListener {
                        dialogOkListener.okOnClick(this, activity)
                    }
//                crossIv.setOnClickListener {
//                    dialog.dismiss()
//                }
                    show()
                }
            }
        }

        //Order Cancel type dialog



        //Info Type dialog
        fun showInfoTypeDialog(
            activity: Activity?
        ) {
            if (activity != null) {
                val dialog = Dialog(activity)
                // Include dialog.xml file
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCanceledOnTouchOutside(true)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.info_layout)


                val okTv = dialog.findViewById<TextView>(R.id.okTv)

                okTv.setOnClickListener {
                    dialog.dismiss()

                }
                dialog.show()
            }
        }

        //Info Type dialog with message
        fun showInfoTypeDialogWithMsg(
            activity: Activity?,
            message: String
        ) {
            if (activity != null) {
                val dialog = Dialog(activity)
                // Include dialog.xml file
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(false)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val binding: InfoLayoutWithMsgBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(activity),
                    R.layout.info_layout_with_msg,
                    null,
                    false
                )
                dialog.setContentView(binding.root)


                binding.contTv.text = message
                binding.okTv.setOnClickListener {
                    dialog.dismiss()

                }
                binding.lower.setOnClickListener { dialog.dismiss() }

                dialog.isShowing
                dialog.dismiss()
                dialog.show()
            }
        }


        //Edit email type dialog
        fun showEmailEditPopup(
            activity: Activity?,
            old_email: String?,
            errorMessage: String?,
            dialogEditEmailListener: DialogEditEmailListener
        ) {

            if (activity != null) {
                val dialog = Dialog(activity)
                // Include dialog.xml file
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val binding: ChangeEmailLayoutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(activity),
                    R.layout.change_email_layout,
                    null,
                    false
                )
                dialog.setContentView(binding.root)
                binding.edtOldEmail.setText(old_email)

                if (errorMessage != "") {
                    binding.erroeEmailTv.text = errorMessage.toString()
                    binding.edtNewEmail.requestFocus()
                }

                binding.saveLayout.setOnClickListener {
                    if (binding.edtNewEmail.text.toString().trim().isBlank()) {
                        binding.erroeEmailTv.text =
                            activity.resources.getString(R.string.please_enter_email)
                        binding.edtNewEmail.requestFocus()

                    } else if (!Patterns.EMAIL_ADDRESS.matcher(
                            binding.edtNewEmail.text.toString().trim()
                        )
                            .matches()
                    ) {
                        binding.erroeEmailTv.text =
                            activity.resources.getString(R.string.enter_valid_email_id)
                        binding.edtNewEmail.requestFocus()
                    } else {
                        old_email?.let { it1 ->
                            dialogEditEmailListener.saveOnClick(
                                dialog, activity,
                                it1,
                                binding.edtNewEmail.text.toString(),
                                binding.erroeEmailTv
                            )

                        }

                    }
                }
                binding.edtNewEmail.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        binding.erroeEmailTv.text = ""
                    }

                    override fun afterTextChanged(s: Editable) {}
                })
                binding.crossIv.setOnClickListener { dialog.dismiss() }
                // Set dialog title
                dialog.setTitle("Private Market")
                dialog.dismiss()
                dialog.show()
            }
        }




        @SuppressLint("StaticFieldLeak")
        var dialogPlus: DialogPlus? = null

        //dialogPlus
        fun dialogPlus(
            activity: Activity,
            tagName: String,
            dialogPlusListener: DialogPlusListener,
            tag: String
        ) {

            dialogPlus = DialogPlus.newDialog(activity)
                .setContentHolder(ViewHolder(R.layout.bottom_dialog_layout))
                .setGravity(Gravity.BOTTOM)
                .setMargin(0, 100, 0, 0)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setInAnimation(R.anim.slide_up)
                .setOutAnimation(R.anim.slide_down)
                .create()
            val recyclerView = dialogPlus?.findViewById(R.id.rv_list) as RecyclerView
            val noDataFoundTv = dialogPlus?.findViewById(R.id.noDataFoundTv) as TextView
            val dialogTag = dialogPlus?.findViewById(R.id.tagTv) as TextView
            dialogTag.text = tagName
            dialogPlus?.let {
                dialogPlusListener.setBottomDialogListener(
                    activity,
                    it,
                    recyclerView,
                    noDataFoundTv,
                    tag
                )
            }

        }

        @SuppressLint("StaticFieldLeak")
        var bottomDialogPlus: DialogPlus? = null

        //Set bottom dialog
        fun setBottomDialog(
            activity: Activity,
            tagName: String,
            dialogPlusListener: DialogPlusListener,
            tag: String
        ) {

            bottomDialogPlus = DialogPlus.newDialog(activity)
                .setContentHolder(ViewHolder(R.layout.bottom_dialog_layout))
                .setGravity(Gravity.BOTTOM)
                .setMargin(0, 100, 0, 0)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setInAnimation(R.anim.slide_up)
                .setOutAnimation(R.anim.slide_down)
                .create()
            val recyclerView = bottomDialogPlus?.findViewById(R.id.rv_list) as RecyclerView
            val noDataFoundTv = bottomDialogPlus?.findViewById(R.id.noDataFoundTv) as TextView
            val dialogTag = bottomDialogPlus?.findViewById(R.id.tagTv) as TextView
            dialogTag.text = tagName
            bottomDialogPlus?.let {
                dialogPlusListener.setBottomDialogListener(
                    activity,
                    it,
                    recyclerView,
                    noDataFoundTv,
                    tag
                )
            }


        }



        private var onceDialog: Dialog? = null

        @SuppressLint("SetTextI18n")
        fun popupOnce(
            activity: Activity,
            dialogOnceListener: DialogOnceListener
        ) {
            if (!activity.isFinishing) {

                onceDialog = Dialog(activity)

                onceDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
                onceDialog?.setContentView(R.layout.activity_menu_order_once)
                val width = (activity.resources.displayMetrics.widthPixels)
                val height = (activity.resources.displayMetrics.heightPixels)

                onceDialog?.window?.setLayout(width, height)

                onceDialog?.setCancelable(false)

                val okLayout = onceDialog?.findViewById<RelativeLayout>(R.id.okLayout)

                val crossIv = onceDialog?.findViewById<ImageView>(R.id.iv_cross)
                val backIv = onceDialog?.findViewById<ImageView>(R.id.iv_back)
                val homeIv = onceDialog?.findViewById<ImageView>(R.id.iv_home)
                homeIv?.visibility = View.GONE
                backIv?.visibility = View.GONE
                crossIv?.setOnClickListener {

                    dialogOnceListener.onceClick(onceDialog!!, activity)
                }



                okLayout?.setOnClickListener {

                    dialogOnceListener.onceClick(onceDialog!!, activity)
                }

                onceDialog?.show()


            }
        }

        fun showOrderUpdateDialog(activity: Activity, contentText: String) {
            val orderUpdateDialog = Dialog(activity)
            orderUpdateDialog.window?.setBackgroundDrawableResource(R.color.transparent)
            orderUpdateDialog.setContentView(R.layout.student_added_popup)
            orderUpdateDialog.setCancelable(false)

            val okTv = orderUpdateDialog.findViewById<TextView>(R.id.okTv)
            val contentTv = orderUpdateDialog.findViewById<TextView>(R.id.contentTv)

            contentTv.text = contentText

            okTv.setOnClickListener {
                orderUpdateDialog.dismiss()
            }
            orderUpdateDialog.show()
        }

        fun removeFromFavorites(
            context: Context,
            dialogYesNoListener: DialogYesNoListener
        ){
            val dialog = Dialog(context)
            dialog.window?.let{ window ->
                window.setBackgroundDrawableResource(R.color.transparent)
                window.decorView.setBackgroundResource(R.color.transparent)
                window.setDimAmount(0.0f)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val dialogBinding = RemoveFromFavoriteDialogBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)

            dialogBinding.btnYes.setOnClickListener {
                dialogYesNoListener.yesOnClick(dialog, context as Activity)
            }
            dialogBinding.btnNo.setOnClickListener {
                dialogYesNoListener.noOnClick(dialog, context as Activity)
            }

            dialog.show()
        }

        //clear food item popup
        fun clearFoodItemPopup(
            activity: Activity?,
            msg: String?,
            yesButtonText: String,
            noButtonText: String,
            dialogYesNoListener: DialogYesNoListener
        ) {
            if (activity != null) {
                val dialog = Dialog(activity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCanceledOnTouchOutside(true)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.clear_food_item_yes_no)
                val contextTv = dialog.findViewById<TextView>(R.id.contextTv)
                val yesTv = dialog.findViewById<TextView>(R.id.yesTv)
                val noTv = dialog.findViewById<TextView>(R.id.noTv)

                yesTv.text = yesButtonText
                noTv.text = noButtonText

                val crossIv = dialog.findViewById<View>(R.id.crossIv) as ImageView

                contextTv.text = msg

                yesTv.setOnClickListener {
                    dialogYesNoListener.yesOnClick(dialog, activity)

                }
                noTv.setOnClickListener {
                    dialogYesNoListener.noOnClick(dialog, activity)

                }
                crossIv.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }

        fun repeatFoodItemPopup(
            activity: Activity?,
            nextNewOrderListener: NextNewOrderListener
        ) {
            if (activity != null) {
                val dialog = Dialog(activity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCanceledOnTouchOutside(true)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.repeat_food_item_yes_no)


                val newOrderTv = dialog.findViewById<TextView>(R.id.newOrderTv)
                val confirmTv = dialog.findViewById<TextView>(R.id.confirmTv)
                val cancelTv = dialog.findViewById<TextView>(R.id.cancelTv)


                val cancelIv = dialog.findViewById<ImageView>(R.id.cancelIv)
                cancelIv.setOnClickListener {
                    dialog.dismiss()
                }
                confirmTv.setOnClickListener {
                    nextNewOrderListener.confirmClick(dialog, activity)
                }
                cancelTv.setOnClickListener {
                    dialog.dismiss()
                }


                newOrderTv.setOnClickListener {
                    nextNewOrderListener.newOrderHereClick(dialog, activity)
                }

                dialog.show()
            }
        }

        fun allergenTypeDialog(activity: Activity?, status: Int?, msg: String?) {
            activity?.let { act ->
                if (!act.isFinishing) {
                    val dialog = Dialog(act)
                    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
                    dialog.window?.decorView?.setBackgroundResource(android.R.color.transparent)
                    dialog.window?.setDimAmount(0.1f)
                    dialog.window?.setLayout(
                        act.resources.getDimensionPixelSize(R.dimen._215sdp),
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )


                    val binding: AllergenTypeDialogBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(activity),
                        R.layout.allergen_type_dialog,
                        null,
                        false
                    )

                    dialog.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    binding.contentTv.text = msg

                    // binding.rlOk.isVisible = status==STATUS_ONE


                    dialog.setContentView(binding.root)
                    dialog.setCancelable(true)

                    binding.crossIv.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }

            }
        }
    }
}