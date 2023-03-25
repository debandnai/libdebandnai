package com.merkaaz.app.data.bindingAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import com.chaos.view.PinView
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.merkaaz.app.R
import com.merkaaz.app.data.viewModel.LoginViewModel
import com.merkaaz.app.data.viewModel.OtpViewModel
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass


@BindingAdapter(value = ["app:status", "app:activity"])
fun setLanguageBackGround(
    view: LinearLayout,
    status: Boolean,
    activity: Activity
) {

    if (status)
        view.background = ContextCompat.getDrawable(activity, R.drawable.teal_btn_bg)
    else
        view.background = ContextCompat.getDrawable(activity, R.drawable.white_btn_bg)

}


@BindingAdapter("app:setSelectedSubCategory")
fun setSelectedSubCategory(textView: TextView, isSelected: Boolean) {
    Log.e( "setSelectedSubCategory: ", isSelected.toString())
    if (isSelected) {

        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
        textView.typeface = ResourcesCompat.getFont(textView.context, R.font.open_sans_semi_bold)
        textView.background =
            ContextCompat.getDrawable(textView.context, R.drawable.red_bg_round_corner)

    } else {

        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.dark_blue))
        textView.typeface = ResourcesCompat.getFont(textView.context, R.font.open_sans_regular)
        textView.background =
            ContextCompat.getDrawable(textView.context, R.drawable.white_bg_round_corner)
    }
}

@BindingAdapter("app:setApprovedUserStatus")
fun setApprovedUserStatus(imageView: ImageView, status: Boolean) {
    Log.e( "statusicon status: ", status.toString())
    if (status) {
        imageView.setImageResource(R.drawable.ic_verified_user)
    } else {
        imageView.setImageResource(R.drawable.ic_cross_ioc)
    }
}


@BindingAdapter("app:setSelectedFilterButton")
fun setSelectedFilterButton(textView: TextView, isSelected: Boolean) {
    if (isSelected) {

        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
        textView.typeface = ResourcesCompat.getFont(textView.context, R.font.open_sans_semi_bold)
        textView.background =
            ContextCompat.getDrawable(textView.context, R.drawable.dark_blue_bg_round_corner)

    } else {

        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.dark_blue))
        textView.typeface = ResourcesCompat.getFont(textView.context, R.font.open_sans_regular)
        textView.background =
            ContextCompat.getDrawable(textView.context, R.drawable.white_bg_round_corner)
    }
}


@BindingAdapter("app:setTextViewValueWithAOA")
fun setTextViewValueWithAOA(textView: TextView, dataValue: String) {
    val text =
        "<font color='" + textView.context.getColor(R.color.address_unselect_color) + "'>$dataValue</font><font color='" + textView.context.getColor(
            R.color.teal_700
        ) + "'>" + textView.context.getString(R.string.aoa) + "</font>"
    textView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY);
}

@BindingAdapter("app:setTextViewValueWithPercentage")
fun setTextViewValueWithPercentage(textView: TextView, dataValue: String) {
    val text =
        "<font color='" + textView.context.getColor(R.color.address_unselect_color) + "'>$dataValue</font><font color='" + textView.context.getColor(
            R.color.teal_700
        ) + "'>%</font>"
    textView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY);
}


@BindingAdapter(value = ["app:status", "app:activity"])
fun setDialogBtnTextColor(
    textView: TextView,
    status: Boolean,
    activity: Activity
) {

    if (status)
        textView.setTextColor(ContextCompat.getColor(activity, R.color.white))
    else
        textView.setTextColor(ContextCompat.getColor(activity, R.color.black))
}


@BindingAdapter("app:setTextView")
fun setTextView(
    view: TextView,
    viewModel: ViewModel
) {
//    when (viewModel) {
//        is SignUpViewModel -> viewModel.setTextView(view)
//    }
}

////Spinner for country code with flag image
//@BindingAdapter("app:AppCompatSpinner")
//fun setAppCompatSpinner(
//    view: androidx.appcompat.widget.AppCompatSpinner,
//    viewModel: ViewModel
//) {
//    when (viewModel) {
//        is LoginViewModel -> viewModel.setAppCompatSpinner(view)
//    }
//}

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
                is OtpViewModel -> viewModel.getPin(s)


            }
        }

    })

}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("app:hideKeyboardByClickingOutside")
fun hideKeyboardByClickingOutside(view: View, activity: Activity) {
    //First time hide keyboard
    activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { _, _ ->
            MethodClass.hideSoftKeyboard(activity)
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


@BindingAdapter("app:setAddressInTextView")
fun setAddressInTextView(textView: TextView, address: String?) {
    if (address.isNullOrEmpty() or address.equals(
            textView.context.resources.getString(R.string.press_here_to_set_shop_address),
            true
        )
    ) {
        val content =
            SpannableString(textView.context.resources.getString(R.string.press_here_to_set_shop_address))
        content.setSpan(UnderlineSpan(), 0, 10, 0)
        content.setSpan(StyleSpan(Typeface.BOLD), 0, 10, 0)
        content.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    textView.context,
                    R.color.white
                )
            ), 0, content.length, 0
        )
        content.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(textView.context, R.color.red)),
            content.length - 1,
            content.length,
            0
        )

        textView.text = content
//        textView.setTextColor(ContextCompat.getColor(textView.context,R.color.teal_700))
//        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    } else {

        textView.text = address
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
        //For set TextView Left drawable
        textView.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_location_on_24,
            0,
            0,
            0
        )
    }

}

@BindingAdapter("initMap")
fun initMap(mapView: MapView?, latLng: LatLng?) {
    if (mapView != null) {
        mapView.onCreate(Bundle())
        mapView.getMapAsync(OnMapReadyCallback { googleMap -> // Add a marker
            googleMap.addMarker(MarkerOptions().position(latLng!!).title("Marker in India"))
        })
    }
}

@BindingAdapter("app:openCallDialerOnClick")
fun openCallDialerOnClick(view: View, ph_number: String) {
    view.setOnClickListener {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$ph_number")
        view.context.startActivity(intent)
    }

}

@BindingAdapter(value = ["app:isDiscounted", "app:quantity"])
fun visibilityDiscounted(textView: TextView, isDiscounted: String, quantity: Int?) {
    if ((quantity ?: 0) > 0) {
        if (isDiscounted.equals(Constants.YES, true))
            textView.visibility = View.VISIBLE
        else
            if (textView.id == R.id.tv_price)
                textView.visibility = View.GONE
            else
                textView.visibility = View.INVISIBLE

    } else
        textView.visibility = View.INVISIBLE
}

/*
@BindingAdapter(value = ["app:totalQuantity", "app:quantity"])
fun checkQuantity(textView: TextView, isDiscounted: String, quantity: Int?) {

}
 */

@BindingAdapter(value = ["app:totalQuantity", "app:cartQuantity"])
fun checkQuantity(textView: TextView,  totalQuantity: Int?, quantity: Int?) {
    if (totalQuantity != null && quantity != null) {

        if (quantity >= (totalQuantity.minus(5))) {
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.red))
            textView.visibility = View.VISIBLE
        } else {
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.teal_700))
            if (totalQuantity <= 10)
                textView.visibility = View.VISIBLE
            else
                textView.visibility = View.INVISIBLE
        }



    }
}


/*
@BindingAdapter("app:checkQuantity")
fun checkQuantity(textView: TextView, quantity: Int?) {
    Log.d("aaaaaaa", "checkQuantity: "+quantity)
    textView.text=quantity.toString()
    if ((quantity ?: 0) > (quantity?.minus(5)!!)) {
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.gray))

    } else {
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.teal_700))
    }

}
*/





@BindingAdapter("app:setAddBtnBg")
fun setAddBtnBg(button: AppCompatButton, isOutOfStock: Boolean) {
    if (isOutOfStock)
        button.background =
            ContextCompat.getDrawable(button.context, R.drawable.botton_shape_gray_color)
    else
        button.background = ContextCompat.getDrawable(button.context, R.drawable.teal_btn_bg)
}

@BindingAdapter(value = ["app:isSelected", "app:quantity"])
fun setVariationSelectedBg(imageView: ImageView, isSelected: Boolean, quantity: Int?) {
    if ((quantity ?: 0) > 0) {
        imageView.visibility = View.VISIBLE
        if (isSelected)
            imageView.setImageResource(R.drawable.ic_selected)
        else
            imageView.setImageResource(R.drawable.ic_not_selected)
    } else
        imageView.visibility = View.INVISIBLE
}

@BindingAdapter(value = ["app:isSelected", "app:quantity"])
fun setPackSizeItemBg(linearLayout: LinearLayout, isSelected: Boolean, quantity: Int?) {
    if ((quantity ?: 0) > 0) {
        if (isSelected)
            linearLayout.background =
                ContextCompat.getDrawable(linearLayout.context, R.drawable.teal_rect_bg2)
        else
            linearLayout.background =
                ContextCompat.getDrawable(linearLayout.context, R.drawable.teal_rect_bg)
    } else {
        linearLayout.background =
            ContextCompat.getDrawable(
                linearLayout.context,
                R.drawable.out_of_stock_pack_size_bg_border_green
            )
    }

}

@BindingAdapter("app:setTextViewDropDownIcon")
fun setTextViewDropDownIcon(textView: TextView, variationSize: Int?) {
    variationSize?.let {
        if (variationSize > 1) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_drop_down, 0)
            textView.background =
                ContextCompat.getDrawable(textView.context, R.drawable.black_border_rect_bg)
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            textView.background = ContextCompat.getDrawable(
                textView.context, R.drawable.out_of_stock_pack_size_bg_border_black
            )
        }

    }

}

@BindingAdapter("app:setStrikeThoughLine")
fun setStrikeThoughLine(textView: TextView, isSetStrikeThrough: Boolean) {
    if (isSetStrikeThrough)
        textView.paintFlags =
            textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

@BindingAdapter(value = ["app:isDiscounted", "app:discountedPrice", "app:sellPrice"])
fun setDiscountedPrice(
    textView: TextView,
    isDiscounted: String,
    discountedPrice: String,
    sellPrice: String
) {
    if (isDiscounted.equals(Constants.YES, true))
        textView.text = discountedPrice
    else
        textView.text = sellPrice

}

@BindingAdapter("app:setRedStrikeThroughLine")
fun setRedStrikeThroughLine(textView: TextView, quantity: Int?) {
    quantity?.let {
        if (quantity == 0)
            textView.setBackgroundResource(R.drawable.strike_through_red)
        else
            textView.setBackgroundResource(0)
    }
}

@BindingAdapter("app:clearPinData")
fun clearPinData(pinView: PinView, isClearPinData: Boolean) {
    if (isClearPinData)
        pinView.setText("")

}







