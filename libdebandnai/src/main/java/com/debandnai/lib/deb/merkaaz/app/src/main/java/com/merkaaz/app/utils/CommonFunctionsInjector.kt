package com.merkaaz.app.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.TextView
import com.google.gson.JsonObject
import com.merkaaz.app.BuildConfig
import com.merkaaz.app.R
import com.merkaaz.app.data.model.VariationDataItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CommonFunctions @Inject constructor(
    private val sharedPreff: SharedPreff
) {
    fun commonHeader(): HashMap<String, String> {

        val headers = HashMap<String, String>()
            val token: String? = sharedPreff.get_logindata().token
            token?.let { tokn -> headers.put("Authorization", tokn) }

        return headers
    }

    fun commonJsonData(): JsonObject {
        val jsonObject = JsonObject()
        val appVersion: String = BuildConfig.VERSION_NAME
        jsonObject.addProperty("device_os", Constants.DEVICE_OS)
        jsonObject.addProperty("appversion", appVersion)
        jsonObject.addProperty("lang_name", sharedPreff.getlanguage_id())
        return jsonObject
    }

    fun showErrorMsg(context: Context?, errorCode: Int?, errorMsg: String?) {
        context?.let { ctx ->
            errorCode?.let { errorCode ->
                if (errorCode == 401) {
                    val dialog = Dialog(ctx)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.setContentView(R.layout.dialog_custom)

                    val text = dialog.findViewById<View>(R.id.txt_msg) as TextView
                    text.text = ctx.resources.getString(R.string.seesion_expired_msg)
                    val btn_ok = dialog.findViewById<View>(R.id.btn_green)
                    btn_ok?.setOnClickListener {
                        dialog.dismiss()
                        MethodClass.logOut(ctx, sharedPreff)
                    }
                    dialog.show()
                } else {
                    errorMsg?.let { it1 ->
                        MethodClass.showNetworkOrServerErrorDialog(ctx, it1)
                    }
                }

            }
        }

    }

    fun getaddupdateJsondata(context: Context, prodid: String?, variationitem: VariationDataItem, qty: Int?): JsonObject {
        val json = commonJsonData()
        json.addProperty("cart_id", variationitem.cartId)
        json.addProperty("product_id", prodid)
        json.addProperty("sku", variationitem.sku)
        json.addProperty("sell_price", variationitem.sellPrice)
        json.addProperty("discount_price", variationitem.discountPrice)
        json.addProperty("prod_qty", qty)
        return json
    }

    fun getremoveJsondata(variationitem: VariationDataItem): JsonObject {
        val json = commonJsonData()
        json.addProperty("cart_id", variationitem.cartId)
        return json
    }

}