package ie.healthylunch.app.data.bindingAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.mass.library_calender.CustomCalendarView
import com.stripe.android.view.CardInputWidget
import ie.healthylunch.app.R
import ie.healthylunch.app.data.viewModel.*
import ie.healthylunch.app.ui.PrivacyPolicyActivity
import ie.healthylunch.app.ui.TermsOfUseActivity
import ie.healthylunch.app.utils.Constants.Companion.GREEN
import ie.healthylunch.app.utils.Constants.Companion.NO
import ie.healthylunch.app.utils.Constants.Companion.RED
import ie.healthylunch.app.utils.Constants.Companion.STATUS_EIGHT
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FIVE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FOUR
import ie.healthylunch.app.utils.Constants.Companion.STATUS_MINUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_NINE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_SEVEN
import ie.healthylunch.app.utils.Constants.Companion.STATUS_SIX
import ie.healthylunch.app.utils.Constants.Companion.STATUS_THREE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.YES
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.MethodClass.setStatus
import java.util.*
import kotlin.math.roundToInt


@BindingAdapter("app:requestFocus")
fun requestFocus(view: EditText, requestFocus: Boolean) {
    if (requestFocus)
        view.requestFocus()
}

@BindingAdapter("app:hideTextViewStatusWise")
fun hideTextViewStatusWise(view: TextView, status: Int) {
    if (status == STATUS_ZERO || status == STATUS_MINUS_ONE) {
        if (view.id == R.id.tv_off)
            view.visibility = View.VISIBLE
        else
            view.visibility = View.GONE
    } else {
        if (view.id == R.id.tv_off)
            view.visibility = View.GONE
        else
            view.visibility = View.VISIBLE
    }
}

@BindingAdapter("app:setNotificationSettingLayoutBackGround")
fun setNotificationSettingLayoutBackGround(view: RelativeLayout, status: Int) {
    if (status == STATUS_ZERO || status == STATUS_MINUS_ONE)
        view.background =
            ContextCompat.getDrawable(view.context, R.drawable.background_relative_green)
    else
        view.background =
            ContextCompat.getDrawable(view.context, R.drawable.background_relative_red)

}

@BindingAdapter("app:setPriceLayoutBackground")
fun setPriceLayoutBackground(view: CardView, isChecked: Boolean) {
    if (isChecked) {
        view.setBackgroundColor(
            ContextCompat.getColor(view.context, R.color.order_menu_dropdown_bg_stroke)
        )
        view.cardElevation = 120f
    } else {
        view.setBackgroundColor(Color.WHITE)
        view.cardElevation = 1f
    }

}

@BindingAdapter("app:setQuickViewLayoutVisibilityWithOrder")
fun setQuickViewLayoutVisibilityWithOrder(view: ImageView, status: Int) {
    if (status == STATUS_ZERO)
        view.visibility = View.GONE
    else {
        view.visibility = View.VISIBLE

    }

}

@BindingAdapter("app:setQuickViewLayoutVisibilityWithOutOrder")
fun setQuickViewLayoutVisibilityWithOutOrder(view: ImageView, status: Int) {
    if (status != STATUS_ZERO)
        view.visibility = View.GONE
    else {
        view.visibility = View.VISIBLE

    }
}

@BindingAdapter("app:setQuickViewLayoutVisibilityWithOutOrder")
fun setQuickViewLayoutVisibilityWithOutOrder(view: TextView, status: Int) {
    if (status != STATUS_ZERO)
        view.visibility = View.GONE
    else {
        view.visibility = View.VISIBLE

    }
}


@BindingAdapter("app:setQuickViewLayoutBackGround")
fun setQuickViewLayoutBackGround(view: View, status: Int) {
    when (view) {
        is RelativeLayout -> {
            when (status) {

                0 -> view.background =
                    ContextCompat.getDrawable(
                        view.context,
                        R.drawable.quickview_background_no_order
                    )
                1 -> {
                    view.background =
                        ContextCompat.getDrawable(view.context, R.drawable.quickview_background)

                }
                2 -> {
                    view.background =
                        ContextCompat.getDrawable(
                            view.context,
                            R.drawable.quickview_orange_background
                        )

                }
            }
        }

    }


}

@BindingAdapter("app:setQuickViewStatusIcon")
fun setQuickViewStatusIcon(view: ImageView, status: Int) {
    if (status == STATUS_ONE) {
        view.setImageResource(R.drawable.white_tick)
    } else {
        view.setImageResource(R.drawable.ic_warning)
    }

}

@BindingAdapter("app:setIsOrderBackground")
fun setIsOrderBackground(view: ConstraintLayout, isOrdered: Int) {
    if (isOrdered == STATUS_ZERO)
        view.background =
            ContextCompat.getDrawable(view.context, R.drawable.rect_right_round_corner_white_bg)
    else {
        view.background =
            ContextCompat.getDrawable(view.context, R.drawable.green_selected_borer)

    }


}

@BindingAdapter("app:setQuickViewOrderMessageSetup")
fun setQuickViewOrderMessageSetup(view: TextView, status: Int) {
    if (status == STATUS_ZERO)
        view.text = view.context.getString(R.string.no_order)
    else if (status == STATUS_ONE) {
        view.text = view.context.getString(R.string.ordered)
    } else if (status == STATUS_TWO) {
        view.text = view.context.getString(R.string.ordered)
    }

}


@RequiresApi(Build.VERSION_CODES.P)
@BindingAdapter(value = ["app:viewModel", "app:activity"])
fun setWebView(view: TextView, viewModel: ViewModel, activity: Activity) {
    when (activity) {
        is PrivacyPolicyActivity -> activity.setTextView()
        is TermsOfUseActivity -> activity.setTextView()

    }
}


@BindingAdapter("app:setFinitePagerContainer")
fun setFinitePagerContainer(
    view: ie.healthylunch.app.utils.coverflow.core.FinitePagerContainer,
    viewModel: ViewModel
) {
    when (viewModel) {
        is DashBoardViewModel -> viewModel.setFinitePagerContainer(view)
    }
}


@BindingAdapter("app:setStudentFinitePagerContainer")
fun setStudentFinitePagerContainer(
    view: ie.healthylunch.app.utils.coverflow.core.FinitePagerContainer,
    viewModel: ViewModel
) {
    when (viewModel) {
        is CalenderViewModel -> viewModel.setStudentFinitePagerContainer(view)
        is DashBoardViewModel -> viewModel.setStudentFinitePagerContainer(view)
        is MenuTemplateViewModel -> viewModel.setStudentFinitePagerContainer(view)
        is ProductViewModel -> viewModel.setStudentFinitePagerContainer(view)
        is FavoritesViewModel -> viewModel.setStudentFinitePagerContainer(view)

    }
}

@BindingAdapter(value = ["app:subsamplingScaleImageView", "app:activity"], requireAll = true)
fun subsamplingScaleImageView(
    view: com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView,
    viewModel: ViewModel,
    activity: Activity
) {
    when (viewModel) {
        is HelpViewModel -> viewModel.setImageView(view, activity)

    }
}


@BindingAdapter(value = ["app:viewModel", "app:activity"], requireAll = true)
fun setViewPager(view: ViewPager2, viewModel: ViewModel, activity: Activity) {
    when (viewModel) {
        is CalenderViewModel -> viewModel.setViewPager(view, activity)
    }
}



@BindingAdapter("app:calender")
fun calender(view: CustomCalendarView, viewModel: ViewModel) {
    when (viewModel) {
        is CalenderViewModel -> viewModel.setCalender(view)

    }
}

@BindingAdapter("app:imageview")
fun imageview(view: ImageView, viewModel: ViewModel) {
    when (viewModel) {
        //is MenuTemplateViewModel -> viewModel.setImageView(view)
        //is QuickViewForStudentViewModel -> viewModel.setImageView(view)

    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setCurrencyWithPrice")
fun setCurrencyWithPrice(textView: TextView, price: String) {
    textView.text = "€ $price"
}


@BindingAdapter(value = ["app:setRecyclerView", "app:activity"], requireAll = false)
fun setRecyclerView(view: RecyclerView, viewModel: ViewModel, activity: Activity) {
    when (viewModel) {
        /*is EditStudentAllergenViewModel -> {
            viewModel.setRecyclerView(view, activity)
            viewModel.getAllergenListResponse(view, activity)
        }*/
        /*is AllergenViewModel -> {
            viewModel.setRecyclerView(view, activity)
            viewModel.allergenListApiCall(activity)
        }*/
        /*is NotificationOnOffViewModel -> {
            viewModel.setRecyclerView(view, activity)
            viewModel.notificationSettingsListApiCall(view, activity)
        }*/
        /*is ProductViewModel -> {
            viewModel.setRecyclerView(view)
            viewModel.getProductLitByMenuTemplate()
        }*/

        /*is MenuTemplateViewModel -> {
            viewModel.setRecyclerView(view)
            viewModel.getMenuTemplateResponse()
        }*/
        /*is QuickViewForStudentViewModel -> {
            viewModel.setRecyclerView(view)
            viewModel.adapterNotifyDataSetChanged(view)
        }*/
        is AddNewCardViewModel -> {
            viewModel.setRecyclerView(view, activity)
        }
        /*is TransactionListViewModel -> {
            viewModel.setRecyclerView(view, activity)
            viewModel.adapterNotifyDataSetChanged()
        }*/


    }
}


/*@BindingAdapter("app:setDateBoxBg")
fun setDateBoxBg(view: TextView, isOrderAvailable: Boolean) {
    if (isOrderAvailable)
        view.setBackgroundResource(R.drawable.calender_date_background_green)
    else
        view.setBackgroundResource(R.drawable.calender_date_background_red)

}*/


@BindingAdapter("app:hideErrorTextView")
fun hideErrorTextView(view: TextView, isVisible: Boolean) {
    if (isVisible)
        view.visibility = View.VISIBLE
    else
        view.visibility = View.INVISIBLE
}

@BindingAdapter("app:hideView")
fun hideView(view: View, isVisible: Boolean) {
    if (isVisible)
        view.visibility = View.VISIBLE
    else
        view.visibility = View.INVISIBLE
}

@BindingAdapter("app:visibleOrGone")
fun visibleOrGone(view: View, isVisible: Boolean) {
    if (isVisible)
        view.visibility = View.VISIBLE
    else
        view.visibility = View.GONE
}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("app:hideKeyboardByClickingOutside")
fun hideKeyboardByClickingOutside(view: View, activity: Activity?) {
    //First time hide keyboard
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity)
            false
        }
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView: View = view.getChildAt(i)
            hideKeyboardByClickingOutside(innerView, activity)
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("app:hideToolTipsByClickingOutside")
fun hideToolTipsByClickingOutside(view: View, viewModel: ViewModel) {

    // Set up touch listener for non-text box views to hide keyboard.
    view.setOnTouchListener { _, _ ->
        when (viewModel) {

            /*is MenuTemplateViewModel -> {
                if (!(view.id == R.id.infoIv ||
                            view.id == R.id.itemInfoIv)
                ) {
                    if (!viewModel.shouldToolTipsShow)
                        viewModel.dismissAllToolTips()
                }
            }*/
        }

        false
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView: View = view.getChildAt(i)
            hideToolTipsByClickingOutside(innerView, viewModel)
        }
    }
}


private fun hideSoftKeyboard(activity: Activity?) {
    activity?.let {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken,
                0
            )
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("app:hidePriceDropDownList")
fun hidePriceDropDownList(view: View, viewModel: ViewModel) {
    when (viewModel) {
        is AddNewCardViewModel -> {

            if (!(view.id == R.id.price_dropdown ||
                        view.id == R.id.price_button_select ||
                        view.id == R.id.iv_class_dropdown_up ||
                        view.id == R.id.price_dropdown_layout ||
                        view.id == R.id.price_drop_down_list)
            ) {
                Log.e("TAG", "hidePriceDropDownList: ")
                view.setOnTouchListener { _, _ ->
                    if (viewModel.dropDownListVisible.value == true)
                        viewModel.dropDownListVisible.value = false
                    false
                }
            }


            //If a layout container, iterate over children and seed recursion.
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val innerView: View = view.getChildAt(i)
                    hidePriceDropDownList(innerView, viewModel)
                }
            }
        }

    }
}

@BindingAdapter("app:setCardAddedState")
fun setCardAddedState(view: View, isCardAdded: Boolean) {
    when (view) {
        is RelativeLayout -> {
            if (isCardAdded) {
                view.setBackgroundResource(R.drawable.wizard_card_added_success_bg)
                view.isEnabled = false
            } else {
                view.setBackgroundResource(R.drawable.background_textview_green)
                view.isEnabled = true
            }

        }
        is TextView -> {
            if (view.id == R.id.tv_wallet_addCard) {
                if (isCardAdded)
                    view.visibility = View.GONE
                else
                    view.visibility = View.VISIBLE
            } else if (view.id == R.id.tv_top_up_later) {
                if (isCardAdded)
                    view.text = view.context.getString(R.string.top_up_now)
                else
                    view.text = view.context.getString(R.string.top_up_later)
            } else {
                if (isCardAdded)
                    view.visibility = View.VISIBLE
                else
                    view.visibility = View.GONE
            }
        }
        is ImageView -> {
            if (isCardAdded)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.GONE
        }
    }

}

@BindingAdapter(value = ["app:isCardAdded", "app:brandName"], requireAll = true)
fun setBrandImage(card_brand_img: ImageView, isCardAdded: Boolean, brandName: String) {
    if (isCardAdded) {
        card_brand_img.visibility = View.VISIBLE
        if (!brandName.equals("", ignoreCase = true)) {
            when {
                brandName.equals("Visa", ignoreCase = true) -> {
                    card_brand_img.setImageResource(R.drawable.card_visa)
                }
                brandName.equals("MasterCard", ignoreCase = true) -> {
                    card_brand_img.setImageResource(R.drawable.card_mastercard)
                }
                brandName.equals("American Express", ignoreCase = true) -> {
                    card_brand_img.setImageResource(R.drawable.card_american_express)
                }
                brandName.equals("Discover", ignoreCase = true) -> {
                    card_brand_img.setImageResource(R.drawable.card_discover)
                }
                brandName.equals("Diners Club", ignoreCase = true) -> {
                    card_brand_img.setImageResource(R.drawable.card_diners_club)
                }
                brandName.equals("JCB", ignoreCase = true) -> {
                    card_brand_img.setImageResource(R.drawable.card_jcb)
                }
            }
        }
    } else
        card_brand_img.visibility = View.GONE

}

@BindingAdapter("app:setEnabled")
fun setEnabled(view: View, enabled: Boolean) {
    view.isEnabled = enabled
}

@BindingAdapter("app:addTextChangeListener")
fun addTextChangeListener(view: EditText, viewModel: ViewModel) {

    view.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            when (viewModel) {
                is LoginViewModel -> viewModel.invisibleErrorTexts()

                is ParentRegistrationStepOneViewModel -> viewModel.invisibleErrorTexts()

                is ParentRegistrationStepTwoViewModel -> viewModel.invisibleErrorTexts()

                is ParentRegistrationOtpViewModel -> viewModel.invisibleErrorTexts()

                is AddNewStudentStepTwoViewModel -> viewModel.invisibleErrorTexts()

                is ForgotPasswordStepOneViewModel -> viewModel.invisibleErrorTexts()

                is ForgotPasswordStepTwoViewModel -> viewModel.invisibleErrorTexts()

                is EditStudentProfileViewModel -> viewModel.invisibleErrorTexts()

                is ResetPasswordViewModel -> viewModel.invisibleErrorTexts()

                is EditParentProfileViewModel -> viewModel.invisibleErrorTexts()

                is ChangePasswordActivityViewModel -> viewModel.invisibleErrorTexts()

                is AddVoucherCodeViewModel -> viewModel.invisibleErrorTexts()
            }
        }

    })

}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@BindingAdapter("app:setCardInputWidget")
fun setCardInputWidget(cardInputWidget: CardInputWidget, viewModel: AddNewCardViewModel) {
    cardInputWidget.postalCodeEnabled = false

    //prevent scan a new card pop up while focus the cardInput edittext widget
    //cardInputWidget.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS


    cardInputWidget.setCardNumberTextWatcher(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            viewModel.invisibleErrorTexts()
        }

        override fun afterTextChanged(editable: Editable) {

            if (Objects.equals(editable, null))
                viewModel.cardNumber = ""
            else
                viewModel.cardNumber = editable.toString()
        }
    })

    cardInputWidget.setExpiryDateTextWatcher(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            viewModel.invisibleErrorTexts()
        }

        override fun afterTextChanged(editable: Editable) {

            if (Objects.equals(editable, null))
                viewModel.expDate = ""
            else
                viewModel.expDate = editable.toString()
        }
    })

    cardInputWidget.setCvcNumberTextWatcher(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            viewModel.invisibleErrorTexts()
        }

        override fun afterTextChanged(editable: Editable) {

            if (Objects.equals(editable, null))
                viewModel.cvc = ""
            else
                viewModel.cvc = editable.toString()
        }
    })

    //check validate card details

    //check validate card details
    cardInputWidget.setCardValidCallback { b, set ->
        viewModel.setValidateAllCardDetails()
        if (!b) {
            Log.d("CardInputWidget", "setCardInputWidget:  $b  $set ")
            if (!viewModel.cardNumber.equals("", ignoreCase = true) && set.toString()
                    .contains("Number")
            ) {
                /*setInvisibleAllErrorTextView();
                        error_card.setVisibility(View.VISIBLE);
                        error_card.setText("Please enter valid card number");*/
                viewModel.cardInvalid = true
            } else if (!viewModel.expDate.equals(
                    "",
                    ignoreCase = true
                ) && viewModel.expDate.length >= 4 && set.toString().contains("Expiry")
            ) {
                viewModel.expDateInvalid = true
            } else if (!viewModel.cvc.equals("", ignoreCase = true) && set.toString()
                    .contains("Cvc")
            ) {
                viewModel.cvcInvalid = true
            }
        }
    }

}


@BindingAdapter("app:setPromotionAlertBg")
fun setPromotionAlertBg(view: ImageView?, isActive: Boolean) {
    if (isActive)
        view?.setImageDrawable(
            ContextCompat.getDrawable(
                view.context,
                R.drawable.ic_product_active
            )
        )
    else
        view?.setImageDrawable(
            ContextCompat.getDrawable(
                view.context,
                R.drawable.ic_product_inactive
            )
        )

}



@BindingAdapter("app:setBackGroundByStatus")
fun setBackGroundByStatus(view: ImageView?, isMapped: Int) {
    if (isMapped == 1)
        view?.setImageDrawable(
            ContextCompat.getDrawable(
                view.context,
                R.drawable.ic_product_active
            )
        )
    else
        view?.setImageDrawable(
            ContextCompat.getDrawable(
                view.context,
                R.drawable.ic_product_inactive
            )
        )
}

@SuppressLint("SetTextI18n")
@BindingAdapter(
    value = ["app:transactionType", "app:transactionFor", "app:studentFirstName", "app:studentLastName"],
    requireAll = false
)
fun setTransactionItemState(
    view: View,
    transactionType: String,
    transactionFor: String,
    studentFirstName: String?,
    studentLastName: String
) {
    when (view) {
        is TextView -> {
            if (transactionType.equals("debited", true)
                && transactionFor.equals("Student Order", true)
                && !studentFirstName.isNullOrEmpty()
            ) {
                if (view.id == R.id.transaction_history)
                    view.text = "Money $transactionType \nfor $studentFirstName $studentLastName"

                if (view.id == R.id.tv_transaction_amount)
                    view.setTextColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.add_children_color_error
                        )
                    )


            } else if (transactionType.equals("debited", true)) {
                if (view.id == R.id.transaction_history)
                    view.text = "Money $transactionType \nby Admin"

                if (view.id == R.id.tv_transaction_amount)
                    view.setTextColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.add_children_color_error
                        )
                    )

            } else {
                if (view.id == R.id.transaction_history)
                    view.text = "Money $transactionType \nto Wallet"

                if (view.id == R.id.tv_transaction_amount)
                    view.setTextColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.amount_text_color_green
                        )
                    )


            }

        }
        is ImageView -> {
            if (view.id == R.id.iv_info) {
                if (transactionType.equals("debited", true)
                    && transactionFor.equals("Student Order", true)
                    && !studentFirstName.isNullOrEmpty()
                ) {
                    view.visibility = View.VISIBLE
                    view.setImageResource(R.drawable.info_icon)

                } else {
                    view.visibility = View.GONE
                }
            }
        }
    }

}

@BindingAdapter("app:setFormattedTransactionDate")
fun setFormattedTransactionDate(view: TextView, transactionTime: String) {

    view.text = MethodClass.getFormattedDateTimeForTransactionList(
        transactionTime
    )
}

@BindingAdapter("app:setCallLink")
fun setCallLink(textView: TextView, viewModel: ViewModel) {
    when (viewModel) {
        is FeedBackViewModel -> {
            if (!Objects.equals(textView, null)) {
                Linkify.addLinks(
                    textView,
                    Patterns.PHONE,
                    "tel:",
                    Linkify.sPhoneNumberMatchFilter,
                    Linkify.sPhoneNumberTransformFilter
                )
                textView.movementMethod = LinkMovementMethod.getInstance()
            }

        }
    }
}

@BindingAdapter("app:setEnabledView")
fun setEnabledView(view: View, isEnabled: Boolean) {
    view.isEnabled = isEnabled

}

@BindingAdapter(
    value = ["app:isFree", "app:isDelivered", "app:isPending", "app:isFutureDate"],
    requireAll = true
)
fun setBottomCalendarTextColor(
    textView: TextView,
    isFree: Boolean,
    isDelivered: Boolean,
    isPending: Boolean,
    isFutureDate: Boolean
) {
    if (!isFree && !isDelivered && !isPending && !isFutureDate)
        textView.setTextColor(
            ContextCompat.getColor(
                textView.context,
                R.color.bottom_calendar_normal_text_color
            )
        )
    else
        textView.setTextColor(
            ContextCompat.getColor(
                textView.context,
                R.color.bottom_calendar_delivered_text_color
            )
        )

}

@BindingAdapter("app:setDashBoardOrderAmountPrice")
fun setDashBoardOrderAmountPrice(textView: TextView, totalPriceStr: String?) {
    var totalPrice = 0.00f
    if (totalPriceStr?.isNotEmpty() == true)
        try {
            totalPrice = totalPriceStr.toFloat()
        } catch (e: java.lang.Exception) {
        }
    textView.text = "€ ${String.format("%.2f", totalPrice)}"
}

@BindingAdapter(value = ["app:totalCalorieInt", "app:respectiveCaloriesInt"])
fun setCalorie(view: View, totalCalorieInt: Int?, respectiveCaloriesInt: Int?) {
    //Calorie
    var respectiveCalorie = 0F
    var totalCalorie = 0F
    try {
        respectiveCalorie = respectiveCaloriesInt?.toFloat() ?: 0F
        totalCalorie = totalCalorieInt?.toFloat() ?: 0F
    } catch (e: Exception) {
        e.printStackTrace()
    }
    when (view) {
        is TextView -> {
            if (view.id == R.id.countedCalorieTv)
                view.text = "${respectiveCalorie.roundToInt()}/"
//            else if (view.id == R.id.totalCalorieTv)
//                view.text = "" + totalCalorieInt
        }
        is ProgressBar -> {
            if (view.id == R.id.progressBarOne)
                view.progress = ((respectiveCalorie / totalCalorie) * 100).toInt()
        }
    }
}

@BindingAdapter(value = ["app:totalSugarInt", "app:respectiveSugarInt"])
fun setSugar(view: View, totalSugarInt: Int?, respectiveSugarInt: Int?) {
    //Sugar
    var respectiveSugar = 0F
    var totalSugar = 0F
    try {
        respectiveSugar = respectiveSugarInt?.toFloat() ?: 0F
        totalSugar = totalSugarInt?.toFloat() ?: 0F
    } catch (e: Exception) {
        e.printStackTrace()
    }
    when (view) {
        is TextView -> {
            if (view.id == R.id.countedSugarTv)
                view.text = "${respectiveSugar.roundToInt()}/"
//            else if (view.id == R.id.totalSugarTv)
//                view.text = "" + totalSugarInt
        }
        is ProgressBar -> {
            if (view.id == R.id.progressBarTwo)
                view.progress = ((respectiveSugar / totalSugar) * 100).toInt()
        }
    }
}

@BindingAdapter("app:setFoodImage")
fun setFoodImage(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Glide.with(imageView.context)
            .load(imgUrl)
            .placeholder(R.drawable.menu_placeholder_img)
            .error(R.drawable.menu_placeholder_img)
            .into(imageView)
    }
}

@BindingAdapter("app:setPackingTypeIcon")
fun setPackingTypeIcon(imageView: ImageView, packingType: Int?) {
    packingType?.let {
        when (packingType) {
            1 -> {
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.ic_recycle)
            }
            2 -> {
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(R.drawable.compostable_packing_icon)
            }
            else -> imageView.visibility = View.INVISIBLE
        }

    }
}

@BindingAdapter(value = ["app:orderStatusMsg", "app:isHoliday", "app:orderId"])
fun setOrderStatusMsg(view: View, orderStatusMsg: Int?, isHoliday: Boolean?, orderId: Int?) {
    if (isHoliday == true /*&& orderId== STATUS_ZERO*/) {

        (view as? ImageView)?.setImageResource(R.drawable.ic_warning)
        with(view as? TextView) {
            this?.text = view.context.resources.getString(R.string.calendar_off_in_capital)
            this?.setTextColor(ContextCompat.getColor(view.context, R.color.red5))
        }


    } else {
        orderStatusMsg?.let {
            when (view) {
                is ImageView -> {
                    when (orderStatusMsg) {
                        STATUS_ZERO, STATUS_ONE, STATUS_THREE, STATUS_FOUR, STATUS_SIX, STATUS_NINE -> view.setImageResource(
                            R.drawable.ic_warning
                        )
                        STATUS_FIVE, STATUS_TWO, STATUS_SEVEN, STATUS_EIGHT -> view.setImageResource(
                            R.drawable.ic_green_tick_order_items
                        )
                    }
                }
                /*
                    order_status_msg = 0;   //No Order Placed
                    order_status_msg = 1;   //Calender Off
                    order_status_msg = 2;   //Lunch Please
                    order_status_msg = 3;   //Auto top up failed
                    order_status_msg = 4;   //Top Up Your Account
                    order_status_msg = 5;   //Order will process at 12 noon
                    order_status_msg = 6;  //No Lunch
                 */
                is TextView -> {
                    when (orderStatusMsg) {
                        STATUS_ZERO -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.no_order_placed_in_capital),
                                ContextCompat.getColor(view.context, R.color.red5)
                            )
                        }
                        STATUS_ONE -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.calendar_off_in_capital),
                                ContextCompat.getColor(view.context, R.color.red5)
                            )
                        }
                        STATUS_TWO -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.lunch_please),
                                ContextCompat.getColor(view.context, R.color.font_green3)
                            )
                        }
                        STATUS_THREE -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.auto_top_up_failed_in_capital),
                                ContextCompat.getColor(view.context, R.color.red5)
                            )
                        }
                        STATUS_FOUR -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.top_up_your_account_in_capital),
                                ContextCompat.getColor(view.context, R.color.red5)
                            )
                        }
                        STATUS_FIVE -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.order_will_process_at_12_noon),
                                ContextCompat.getColor(view.context, R.color.font_green3)
                            )
                        }
                        STATUS_SIX -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.no_lunch_in_capital),
                                ContextCompat.getColor(view.context, R.color.red5)
                            )
                        }
                        STATUS_SEVEN -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.order_placed_in_capital),
                                ContextCompat.getColor(view.context, R.color.font_green3)
                            )
                        }
                        STATUS_EIGHT -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.order_will_process_at_12_midnight),
                                ContextCompat.getColor(view.context, R.color.font_green3)
                            )
                        }
                        STATUS_NINE -> {
                            view.setStatus(
                                view.context.resources.getString(R.string.processing),
                                ContextCompat.getColor(view.context, R.color.red5)
                            )
                        }
                    }
                }

            }
        }
    }
}

@BindingAdapter(value = ["app:setDashBoardOrderMenuBg", "app:setOrderStatusBg", "app:setDashBoardDayOffBg", "app:setLateOrderBg", "app:setIsHolidayBg"])
fun setDashBoardOrderMenuBg(
    cardView: MaterialCardView,
    orderStatus: String?,
    orderStatusBg: Int?,
    setDashBoardDayOffBg: String?,
    setLateOrderBg: String?,
    setIsHolidayBg: Boolean?
) {
    orderStatus?.let {

        if (orderStatus.equals(RED, true)) {
            if (setDashBoardDayOffBg.equals(YES, true)) {
                if (orderStatusBg == 0) {
                    //cardView.setBackgroundResource(R.drawable.order_item_day_off_bg)
                    setCardBackgroundColor(cardView, R.color.red_bg_1)
                    setCardStrokeColor(cardView, R.color.red_4)
                } else {
                    //cardView.setBackgroundResource(R.drawable.order_orange_bg)
                    setCardBackgroundColor(cardView, R.color.orange_bg_1)
                    setCardStrokeColor(cardView, R.color.orange_1)
                }
            } else {
                if (orderStatusBg == 0) {
                    if ((setLateOrderBg?.isNotEmpty() == true && setLateOrderBg.equals(
                            STATUS_ONE.toString(),
                            true
                        ))){
                        setCardBackgroundColor(cardView, R.color.red_color_bg)
                        setCardStrokeColor(cardView, R.color.red_color_border)
                    } else{
                        //cardView.setBackgroundResource(R.drawable.order_item_no_order_bg)
                        setCardBackgroundColor(cardView, R.color.light_blue_2)
                        setCardStrokeColor(cardView, R.color.deep_blue)
                    }
                } else {
                    //cardView.setBackgroundResource(R.drawable.order_orange_bg)
                    setCardBackgroundColor(cardView, R.color.orange_bg_1)
                    setCardStrokeColor(cardView, R.color.orange_1)
                    //cardView.setBackgroundResource(R.drawable.order_item_calendar_off_bg)
                }
            }
//            if (orderStatusBg == 0 && setDashBoardDayOffBg.equals(NO, true)){
//                cardView.setBackgroundResource(R.drawable.order_item_no_order_bg)
//            } else{
//                cardView.setBackgroundResource(R.drawable.order_item_calendar_off_bg)
//            }
        } else if (orderStatus.equals(GREEN, true)) {
            //cardView.setBackgroundResource(R.drawable.order_item_bg)
            if (setIsHolidayBg == true){
                setCardBackgroundColor(cardView, R.color.orange_bg_1)
                setCardStrokeColor(cardView, R.color.orange_1)
            } else{
                setCardBackgroundColor(cardView, R.color.green_7)
                setCardStrokeColor(cardView, R.color.green_6)
            }
        } else{
            //cardView.setBackgroundResource(R.drawable.order_orange_bg)
            setCardBackgroundColor(cardView, R.color.orange_bg_1)
            setCardStrokeColor(cardView, R.color.orange_1)
        }
    }
}

fun setCardBackgroundColor(view: MaterialCardView, color: Int) {
    view.setCardBackgroundColor(ContextCompat.getColor(view.context, color))
}

fun setCardStrokeColor(view: MaterialCardView, color: Int) {
    view.strokeColor = ContextCompat.getColor(view.context, color)
}


@BindingAdapter(value = ["app:setDashBoardOrderTextColor", "app:setOrderStatusTextColor", "app:setDashBoardDayOffTextColor"])
fun setDashBoardOrderTextColor(
    textView: TextView,
    orderStatus: String?,
    OrderStatusTextColor: Int?,
    DashBoardDayOffTextColor: String?
) {
    orderStatus?.let {
//        if (orderStatus.equals(RED, true))
//            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.red5))
        if (orderStatus.equals(RED, true)) {
            if (DashBoardDayOffTextColor.equals(YES, true)) {
                if (OrderStatusTextColor == 0) {
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.gray_3))
                } else {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            textView.context,
                            //R.color.slate_color
                                    R.color.orange_1
                        )
                    )
                }
            } else {
                if (OrderStatusTextColor == 0) {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            textView.context,
                            R.color.deep_blue
                        )
                    )
                } else {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            textView.context,
                            R.color.orange_1
                        )
                    )
                    //textView.setTextColor(ContextCompat.getColor(textView.context, R.color.red5))
                }
            }
//            if (OrderStatusTextColor == 0 && DashBoardDayOffTextColor.equals(NO, true)){
//                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.deep_blue))
//            } else{
//                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.red5))
//            }
        } else if (orderStatus.equals(GREEN, true))
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.green_3))
        else
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.orange_1))

    }
}


@BindingAdapter(value = ["app:orderStatus", "app:holidayStatus", "app:dayOff"])
fun showHideOrderDetailsLayout(
    linearLayout: LinearLayout,
    orderStatus: String?,
    holidayStatus: Int?,
    dayOff: String?,
) {
    var llOrderDetailsLayout: LinearLayout? = null
    var llCalendarOffLayout: LinearLayout? = null
    var llNoOrderPlacedLayout: LinearLayout? = null
    when (linearLayout.id) {
        R.id.llOrderDetailsLayout ->
            llOrderDetailsLayout = linearLayout
//        R.id.llCalendarOffLayout ->
//            llCalendarOffLayout = linearLayout
        R.id.llNoOrderPlacedLayout ->
            llNoOrderPlacedLayout = linearLayout
    }

    orderStatus?.let {
        holidayStatus?.let {
            if (orderStatus.equals(RED, true) && dayOff.equals(YES, true)) {
                llOrderDetailsLayout?.isVisible = false
                llCalendarOffLayout?.isVisible = false
                llNoOrderPlacedLayout?.isVisible = false
            } else if (orderStatus.equals(RED, true) && holidayStatus == STATUS_ONE) {
                llOrderDetailsLayout?.isVisible = false
                llCalendarOffLayout?.isVisible = true
                llNoOrderPlacedLayout?.isVisible = false
            } else if (orderStatus.equals(RED, true) && holidayStatus == STATUS_ZERO) {
                llOrderDetailsLayout?.isVisible = false
                llCalendarOffLayout?.isVisible = false
                llNoOrderPlacedLayout?.isVisible = true
            } else {
                llOrderDetailsLayout?.isVisible = true
                llCalendarOffLayout?.isVisible = false
                llNoOrderPlacedLayout?.isVisible = false
            }
        }
    }
}

@BindingAdapter("app:setHungryImage")
fun setHungryImage(imageView: ImageView, orderStatus: String?) {
    orderStatus?.let {
        if (orderStatus.equals(GREEN, true))
            imageView.setImageResource(R.drawable.hungry_with_green_question)
        else
            imageView.setImageResource(R.drawable.hungry_with_yellow_question)
    }
}


@BindingAdapter("app:dashBoardBottomLayoutShowHide")
fun dashBoardBottomLayoutShowHide(view: View, orderStatus: String?) {
    orderStatus?.let {
        if (orderStatus.equals(RED, true))
            view.visibility = View.GONE
        else
            view.visibility = View.VISIBLE

    }
}


@BindingAdapter("app:setCalorieSugarMeter")
fun setCalorieSugarMeter(view: ProgressBar, sugarOrCalorie: String?) {
    if (!sugarOrCalorie.isNullOrBlank()) {
        val strArr = sugarOrCalorie.split("/").toTypedArray()
        if (strArr.size > 1)
            view.progress = ((strArr[0].toFloat() / strArr[1].toFloat()) * 100).toInt()
        else
            view.progress = 0

    } else {
        view.progress = 0
    }

}

@BindingAdapter(value = ["app:orderStatus", "app:dayOff"], requireAll = true)
fun setDashBoardClearOrderBg(textView: TextView, orderStatus: String?, dayOff: String?) {
    orderStatus?.let {
        dayOff?.let {
            if (dayOff.equals(YES, true)) {
                //clear order
                textView.background =
                    AppCompatResources.getDrawable(
                        textView.context,
                        R.drawable.clear_order_menu_button_bg_green_repeat
                    )

                textView.setTextColor(
                    ContextCompat.getColor(
                        textView.context,
                        R.color.clear_order_off_white
                    )
                )
            } else if (orderStatus.equals(RED, true)) {
                textView.background =
                    AppCompatResources.getDrawable(textView.context, R.drawable.orange_bg)
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
//                textView.background =
//                    AppCompatResources.getDrawable(textView.context,R.drawable.clear_order_menu_button_bg)
//                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.light_red_4))
            } else if (orderStatus.equals(GREEN, true)) {
                textView.background =
                    AppCompatResources.getDrawable(textView.context, R.drawable.clear_order_red_bg)
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
            } else {
                textView.background =
                    AppCompatResources.getDrawable(textView.context, R.drawable.orange_bg)
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
            }
        }


    }

}

