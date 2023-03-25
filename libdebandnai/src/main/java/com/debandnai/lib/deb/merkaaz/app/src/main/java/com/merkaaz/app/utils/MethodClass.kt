package com.merkaaz.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.TAG
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.data.model.UnavailableProducts
import com.merkaaz.app.data.model.VariationDataItem
import com.merkaaz.app.databinding.CustomDialogWithTwoButtonsBinding
import com.merkaaz.app.interfaces.CustomDialogYesNoClickListener
import com.merkaaz.app.interfaces.DialogCallback
import com.merkaaz.app.ui.activity.GetStartActivity
import com.merkaaz.app.ui.activity.LoginActivity
import com.merkaaz.app.ui.dialogs.BottomSheetDialogLanguage
import com.merkaaz.app.utils.Constants.DEVICE_OS
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


object MethodClass {

    fun getFullAddressFromLatLon(context: Context, lat: Double, long: Double): String {
        var addresses: List<Address>? = null
        var userCurrentAddress = ""
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            addresses = geocoder.getFromLocation(
                lat,
                long,
                1
            )

            if (addresses != null) {
                if (addresses.isNotEmpty())
                    userCurrentAddress = addresses[0].getAddressLine(0)
            }

            Log.d(TAG, "getFullAddressFromLatLon: $userCurrentAddress\n $lat   $long")
        } catch (e: IOException) {
        }
        return userCurrentAddress


    }






    /*private fun wrongLocationCheck(context: Context, lat: Double, long: Double): String? {
        val userCurrentAddress: String? = null
        val geocoder = Geocoder(context, Locale.getDefault())

        val addresses: List<Address> = geocoder.getFromLocation(
            lat,
            long,
            1
        )

        return userCurrentAddress


    }*/


    fun getCityName(context: Context, lat: Double, long: Double): String {
        var addresses: List<Address>? = null
        var cityName = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {

            addresses = geocoder.getFromLocation(
                lat,
                long,
                1
            )


            if (addresses?.isNotEmpty() == true)

                addresses[0].locality?.let {
                    cityName = addresses[0].locality
                    println("address....$cityName")
                }else println("address.. no address")
        } catch (e: IOException) {
        }
        return cityName
    }

    fun showToastMsg(context: Context?, msg: String?) {
        context?.let {
            msg?.let {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getStateName(context: Context, lat: Double, long: Double): String {
        var stateName = ""
        var addresses: List<Address>? = null
        val geocoder = Geocoder(context, Locale.getDefault())
        try {


            addresses = geocoder.getFromLocation(
                lat,
                long,
                1
            )

            if (addresses?.isNotEmpty() == true)
                addresses[0].adminArea?.let {
                    stateName = addresses[0].adminArea
                }
        } catch (_: IOException) {
        }
        return stateName
    }

    fun getPinCode(context: Context, lat: Double, long: Double): String {
        var pinCode = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(
                lat,
                long,
                1
            )

            if (addresses?.isNotEmpty() == true)
                addresses[0].postalCode?.let {
                    pinCode = addresses[0].postalCode
                }
        } catch (e: IOException) {
        }

        return pinCode
    }

    fun getLatLonFromAddress(act: Activity?, strAddress: String?): LatLng? {
        var address: MutableList<Address>? = null
        val coder = act?.let { Geocoder(it) }
        var latLng: LatLng? = null
        try {
            if (coder != null) {
                address = strAddress?.let { coder.getFromLocationName(it, 5) } ?: return null

                if (address.isNotEmpty()) {
                    val location = address[0]
                    location.latitude
                    location.longitude
                    latLng = LatLng(location.latitude, location.longitude)
                }
            }


        } catch (_: IOException) {
        }
        return latLng


    }

    fun get_error_method(err: ResponseBody): String {
        val jsonErr = JSONObject(err.string())
        val gson = Gson()
        val type = object : TypeToken<JsonobjectModel>() {}.type
        val errorResponse: JsonobjectModel? = gson.fromJson(jsonErr.toString(), type)
        return errorResponse?.response?.status?.msg.toString()
    }


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

//        val dialogButton: Button = dialog.findViewById<View>(R.id.btn_dialog) as Button
//        dialogButton.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                dialog.dismiss()
//            }
//        })
//
//        dialog.show()

    }

    fun getAddressList(
        context: Context,
        street_1: String?,
        street_2: String?,
        referencePoint: String?
    ): List<String> {
        val addressList: MutableList<String> = ArrayList()
        if (!street_1.isNullOrBlank())
            addressList.add(street_1)

        if (!street_2.isNullOrBlank())
            addressList.add(street_2)

        addressList.add(context.resources.getString(R.string.luanda))
        addressList.add(context.resources.getString(R.string.angola))

        if (!referencePoint.isNullOrBlank())
            addressList.add(referencePoint)

        return addressList

    }


    @SuppressLint("MissingPermission")
    fun check_networkconnection(ctx: Context?): Boolean {
        var isConnected = false
        ctx?.let {
            val connectivityManager: ConnectivityManager? =
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val activeNetwork = connectivityManager?.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            isConnected = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        return isConnected

    }

    fun logOut(context: Context?, sharedPreff: SharedPreff) {
        context?.let { ctx ->
            sharedPreff.clear_logindata(ctx)
            ctx.startActivity(
                Intent(ctx, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

    }

    fun showBottomSheetDialog(activity: Activity?, tag: String) {
        activity?.let {
            val modalBottomSheet = BottomSheetDialogLanguage(activity)
            when (activity) {
                is GetStartActivity -> {
                    modalBottomSheet.isCancelable = false
                }
            }

            modalBottomSheet.show((activity as AppCompatActivity).supportFragmentManager, tag)
        }

    }

    fun replaceCityPostalCodeStateFromAddress(
        activity: Activity,
        mapAddress: String?,
        latitude: Double?,
        longitude: Double?
    ): String? {
        return mapAddress?.replace(activity.resources.getString(R.string.luanda), "")?.replace(activity.resources.getString(R.string.angola), "")?.replace(
            getCityName(activity, latitude!!, longitude!!), "")?.
        replace(
            getStateName(activity, latitude, longitude), "")?.
        replace(
            getPinCode(activity, latitude, longitude), ""
        )
    }


    fun custom_msg_dialog(ctx: Context?, msg: String?): Dialog? {
        var dialog: Dialog? = null
        ctx?.let {
            dialog = Dialog(it)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setContentView(R.layout.dialog_custom)

            val text = dialog?.findViewById<View>(R.id.txt_msg) as TextView
            text.text = msg
            val btn_ok = dialog?.findViewById<View>(R.id.btn_green)
            btn_ok?.setOnClickListener {
//                when (ctx) {
//                    is AppCompatActivity -> {
//                        when (ctx) {
//                            is ProductDetailsActivity -> {
//                                ctx.finish()
//                            }
//                        }
//                    }
//
//                }
                dialog?.dismiss()
            }
        }
        return dialog

    }



    fun custom_msg_no_order_dialog_callback(ctx: Context?, title: String, msg: String,tag: String,dialogCallback: DialogCallback): Dialog? {
        var dialog: Dialog? = null
        ctx?.let {
            dialog = Dialog(it)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setContentView(R.layout.no_order_dialog_custom)

            val tvTitle = dialog?.findViewById<View>(R.id.tv_title) as TextView
            val tvMsg = dialog?.findViewById<View>(R.id.tv_msg) as TextView
            val btnOk = dialog?.findViewById<Button>(R.id.btn_ok)
            tvTitle.text = title
            tvMsg.text = msg
            btnOk?.text=tag
            btnOk?.setOnClickListener {
                dialog?.dismiss()
                dialogCallback.dialog_clickstate()
            }
            dialog!!.show()
        }
        return dialog

    }


    fun custom_msg_dialog_callback(ctx: Context?,msg: String,dialogCallback: DialogCallback ): Dialog? {
        var dialog: Dialog? = null
        ctx?.let {
            dialog = Dialog(it)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setContentView(R.layout.dialog_custom)

            dialog?.findViewById<SimpleDraweeView>(R.id.img_drawee)?.visibility = View.INVISIBLE
            val text = dialog?.findViewById<View>(R.id.txt_msg) as TextView
            text.text = msg
            val btn_ok = dialog?.findViewById<View>(R.id.btn_green)
            btn_ok?.setOnClickListener {
                dialog?.dismiss()
                dialogCallback.dialog_clickstate()
            }
            dialog!!.show()
        }
        return dialog

    }


    fun custom_msg_dialog_reorder_callback(
        ctx: Context?,
        placeStatus: Int?,
        newCartTotal: String?,
        oldCartTotal: String?,
        unavailableProducts: ArrayList<UnavailableProducts>,
        minOrderValue: String?,
        dialogCallback: DialogCallback
    ): Dialog? {
        var dialog: Dialog? = null
        ctx?.let {
            dialog = Dialog(it)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setContentView(R.layout.dialog_custom_re_order)
            val btn_ok = dialog?.findViewById<View>(R.id.btn_green) as Button
            val btn_ash = dialog?.findViewById<View>(R.id.btn_ash) as Button
            /*val binding: DialogCustomReOrderBinding = DataBindingUtil.inflate(
                LayoutInflater.from(ctx),
                R.layout.dialog_custom_re_order,
                null,
                false
            )*/
            /*var productName=""
            for (product in unavailableProducts)
                productName+="$productName "
            if (unavailableProducts.size>1)
                productName+= ctx.getString(R.string.are)
            else
                productName+= ctx.getString(R.string.is_)*/


            val tvUnAvailableProduct = dialog?.findViewById<View>(R.id.tv_un_available_product) as TextView

            tvUnAvailableProduct.text=ctx.getString(R.string.is_unavailable_and_your_minimum_order_value_should_be)+" "+minOrderValue
            when (placeStatus) {

                1 -> {
                    tvUnAvailableProduct.text=
                        ctx.getString(R.string.products_unavialable)
                    tvUnAvailableProduct.visibility=View.VISIBLE
                }
                2 -> {
                    val msg =ctx.getString(R.string.cart_amount_chng_msg)+" "+oldCartTotal+ctx.getString(R.string.to)+" "+
                            newCartTotal+ctx.getString(R.string.proceed_msg)
                    btn_ash.text = ctx.getString(R.string.no)
                    btn_ok.text = ctx.getString(R.string.yes)
                    tvUnAvailableProduct.text=msg
                    tvUnAvailableProduct.visibility=View.VISIBLE
                    btn_ash.visibility=View.VISIBLE

                }

                3 -> {
                    val msg = ctx.getString(R.string.min_order_msg)+minOrderValue+ctx.getString(R.string.add_more_msg)
                    tvUnAvailableProduct.text=msg
                    tvUnAvailableProduct.visibility=View.VISIBLE
                }
            }

            btn_ok.setOnClickListener {
                dialog?.dismiss()
                dialogCallback.dialog_clickstate()
            }
            btn_ash.setOnClickListener {
                dialog?.dismiss()
            }

            dialog!!.show()
        }
        return dialog

    }

    fun customDialogWithTwoButtons(
        context: Context?,
        title: String?,
        msg: String?,
        yesBtnText: String?,
        noBtnText: String?,
        listener: CustomDialogYesNoClickListener
    ) {
        var dialog: Dialog? = null
        context?.let { ctx ->
            dialog = Dialog(ctx)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: CustomDialogWithTwoButtonsBinding = DataBindingUtil.inflate(
                LayoutInflater.from(ctx),
                R.layout.custom_dialog_with_two_buttons,
                null,
                false
            )
            dialog?.setContentView(binding.root)

            binding.tvTitle.text = title
            binding.tvMsg.text = msg
            binding.btnYes.text = yesBtnText
            binding.btnNo.text = noBtnText

            binding.btnYes.setOnClickListener {
                listener.yesClickListener(dialog)
            }
            binding.btnNo.setOnClickListener {
                listener.noClickListener(dialog)
            }
            dialog?.show()
        }

    }


    fun showNetworkOrServerErrorDialog(context: Context, errorMsg: String) {
        if (errorMsg.equals(Constants.ACCOUNT_DEACTIVATE,true)){
            custom_msg_dialog(
                context,
                context.resources.getString(R.string.account_deactivate)
            )?.show()
        }else if (errorMsg.equals(Constants.HTTP_ERROR, true))
            custom_msg_dialog(
                context,
                context.resources.getString(R.string.network_error_msg)
            )?.show()
        else if (errorMsg.equals(Constants.SERVER_ERROR, true))
            custom_msg_dialog(
                context,
                context.resources.getString(R.string.server_error_msg)
            )?.show()
        else
            custom_msg_dialog(context, errorMsg)?.show()

    }

//    fun setAddress(context: Context?, textView: TextView) {
//        context?.let {
//            val address = StringBuffer()
//            address.append(SharedPreff.get_logindata(context).address1)
//            address.append(", ")
//            if (SharedPreff.get_logindata(context).address2?.trim()?.length != 0) {
//                address.append(SharedPreff.get_logindata(context).address2)
//                address.append(", ")
//            }
//
//            address.append(SharedPreff.get_logindata(context).city)
//            address.append(", ")
//
//            address.append(SharedPreff.get_logindata(context).state)
//            address.append(", ")
//
//            address.append(SharedPreff.get_logindata(context).referencePoint)
//
//
//            textView.text = address.toString()
//        }
//
//    }

    fun NestedScrollView.isVIewVisible(view: View): Boolean {
        val scrollBounds = Rect()
        this.getDrawingRect(scrollBounds)
        val top = view.y
        val bottom = view.height + top
        return scrollBounds.bottom > bottom

    }



    @Throws(IOException::class)
    fun fileCompress(front_fileforApi: File): File? {
        var compressQuality = 0.0
        try {
            if (front_fileforApi.length() / 1024 >= 5124) {
                val difference = front_fileforApi.length() / 1024 - 5124
                val actual_size = front_fileforApi.length() / 1024
                compressQuality = difference.toDouble() / actual_size * 100
                compressQuality = if (difference > 7168) {
                    difference.toDouble() / actual_size * 100
                } else {
                    27.0
                }
                val filePath = front_fileforApi.path
                val bitmap = BitmapFactory.decodeFile(filePath)
                val os: OutputStream = BufferedOutputStream(FileOutputStream(front_fileforApi))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 - compressQuality.toInt(), os)
                os.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return front_fileforApi
    }

    private fun getRealPathFromURI(act: Activity?,contentURI: Uri): String? {
        var result: String? = ""
        act?.let {
            try {
                val _uri = contentURI
                val fileName: String? = getImageName(contentURI, it)
                val file: File? = fileName?.let { it1 -> File(act.externalCacheDir, it1) }
                file?.createNewFile()
                FileOutputStream(file).use { outputStream ->
                    act.contentResolver.openInputStream(contentURI).use { inputStream ->
                        inputStream?.let { it1 -> copyStream(it1, outputStream) } //Simply reads input to output stream
                        outputStream.flush()
                    }
                }
                result = Uri.fromFile(file).path
            } catch (e: java.lang.Exception) {
                e.toString()
            }
        }

        return result
    }

    @SuppressLint("Range")
    fun getImageName(uri: Uri, act: Activity?): String? {
        var fileName: String? = ""
        act?.let {
            if (uri.scheme == "file") {
                fileName = uri.lastPathSegment
            } else {
                var cursor: Cursor? = null
                try {
                    cursor = it.contentResolver.query(
                        uri, arrayOf(
                            MediaStore.Images.ImageColumns.DISPLAY_NAME
                        ), null, null, null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        fileName =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor?.close()
                }
            }
        }

        return fileName
    }

    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }

    fun setLocale(activity: Activity, languageCode: String?) {
        languageCode?.let {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val resources: Resources = activity.resources
            val config: Configuration = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    fun decimalFormat(data: Double): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        return df.format(data)
    }

    fun hideSoftKeyboard(activity: Activity) {
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



