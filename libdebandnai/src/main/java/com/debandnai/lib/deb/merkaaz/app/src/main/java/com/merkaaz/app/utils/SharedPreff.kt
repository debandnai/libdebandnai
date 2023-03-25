package com.merkaaz.app.utils

import android.content.Context
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.utils.Constants.MERKAAZ_SHAREDFREFFRENCE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreff @Inject constructor(@ApplicationContext private val context: Context?){

    fun setPhone(phn: String?) {
        //clearAll(context);
        context?.let {
            val settings =
                context.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            val editor = settings.edit()
            phn?.let { phone ->
                editor.putString(Constants.PHONE, phone)
            }

            // Commit the edits!
            editor.apply()
        }
    }

    fun getPhone(): String {
        var phn: String? = null
        context?.let {
            val shrdprf =
                it.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            phn = shrdprf.getString(Constants.PHONE, "0")
        }
        return phn ?: "0"

    }

    fun setTotalProductCount(totalProductCount: Int?) {
        context?.let {
            val settings =
                context.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            val editor = settings.edit()
            totalProductCount?.let { totalProductCount ->
                editor.putInt(Constants.TOTAL_PRODUCT_COUNT, totalProductCount)
            }
            editor.apply()
        }
    }

    fun getTotalProductCount(): Int? {
        var totalProductCount: Int? = null
        context?.let {
            val shrdprf =
                it.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            totalProductCount = shrdprf.getInt(Constants.TOTAL_PRODUCT_COUNT, 0)
        }
        return totalProductCount ?: 0

    }


    fun setlatlong( lat: Double?, long: Double?) {
        //clearAll(context);
        context?.let {
            val settings =
                context.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            val editor = settings.edit()
            lat?.let {
                editor.putString(Constants.LAT, it.toString())
            }

            long?.let {
                editor.putString(Constants.LONG, it.toString())
            }

            // Commit the edits!
            editor.apply()
        }
    }

    fun getlat(): Double? {
        return context?.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            ?.getString(Constants.LAT, "0.0")?.toDouble()

    }

    fun getlong(): Double? {
        return context?.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            ?.getString(Constants.LONG, "0.0")?.toDouble()

    }


    fun setLanguage( lancode: String?, lang_id: String?) {
        //clearAll(context);
        context?.let {
            val settings =
                context.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            val editor = settings.edit()
            lang_id?.let {
                editor.putString(Constants.LANGUAGE_ID, it)
            }

            lancode?.let {
                editor.putString(Constants.LANGUAGE_CODE, it)
            }

            // Commit the edits!
            editor.apply()
        }
    }

    fun getlanguage_code(): String? {
        return context?.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            ?.getString(Constants.LANGUAGE_CODE, Constants.PORTUGUESE_CODE)

    }

    fun getlanguage_id(): String? {
        return context?.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            ?.getString(Constants.LANGUAGE_ID, "-1")
    }

    fun setfirebase_token(token : String?){
        context?.let {
            val settings = context.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            val editor = settings.edit()
            token?.let {
                editor.putString(Constants.FCMTOKEN, it)
            }
            editor.apply()
        }

    }

    fun getfirebase_token(): String? {
        return context?.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            ?.getString(Constants.FCMTOKEN, "")
    }

    fun store_logindata(loginModel: LoginModel) {
        context?.let {
            val shrdpref = it.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            val editor = shrdpref.edit()
            editor.putString(Constants.TOKEN, loginModel.token)
            editor.putString(Constants.ID, loginModel.id)
            editor.putString(Constants.VENDOR_NAME, loginModel.vendorName)
            editor.putString(Constants.EMAIL, loginModel.email)
            loginModel.isActive?.let { it1 -> editor.putInt(Constants.IS_ACTIVE, it1) }
            editor.putString(Constants.PHONE, loginModel.phone)
            editor.putString(Constants.CITY, loginModel.city)
            editor.putString(Constants.STATE, loginModel.state)
            editor.putString(Constants.ADDRESS1, loginModel.address1)
            editor.putString(Constants.ADDRESS2, loginModel.address2)
            editor.putString(Constants.SHOP_NAME, loginModel.shopName)
            editor.putString(Constants.REFERENCE_POINT, loginModel.referencePoint)
            editor.putString(Constants.PROFILE_IMAGE, loginModel.profileImage)
            editor.putString(Constants.COUNTRY, loginModel.country)
            editor.putString(Constants.LATITUDE, loginModel.latitude)
            editor.putString(Constants.LONGITUDE, loginModel.longitude)
            editor.putString(Constants.TAX_ID, loginModel.taxId)

            editor.apply()

        }
    }

    fun clear_logindata(ctx: Context?) {
        ctx?.let {
            val shrdpref = it.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            val editor = shrdpref.edit()
            editor.putString(Constants.TOKEN, null)
            editor.putString(Constants.ID, null)
            editor.putString(Constants.VENDOR_NAME, null)
            editor.putString(Constants.EMAIL, null)
            editor.putInt(Constants.IS_ACTIVE, 0)
            editor.putString(Constants.PHONE, null)
            editor.putString(Constants.CITY, null)
            editor.putString(Constants.STATE, null)
            editor.putString(Constants.ADDRESS1, null)
            editor.putString(Constants.ADDRESS2, null)
            editor.putString(Constants.REFERENCE_POINT,"")
            editor.putString (Constants.SHOP_NAME, null)
            editor.putString(Constants.PROFILE_IMAGE, null)
            editor.putString(Constants.COUNTRY, null)
            editor.putString(Constants.LATITUDE, null)
            editor.putString(Constants.LONGITUDE, null)
            editor.putString(Constants.TAX_ID, null)

            editor.apply()

        }
    }


    fun get_logindata(): LoginModel {
//        val login = LoginModel()
        return context?.run {
            val shrdpref = this.getSharedPreferences(MERKAAZ_SHAREDFREFFRENCE, Context.MODE_PRIVATE)
            val loginModel = LoginModel().also { login ->
                login.token = shrdpref.getString(Constants.TOKEN, "")
                login.id = shrdpref.getString(Constants.ID, "")
                login.vendorName = shrdpref.getString(Constants.VENDOR_NAME, "")
                login.email = shrdpref.getString(Constants.EMAIL, "")
                login.isActive = shrdpref.getInt(Constants.IS_ACTIVE, 0)
                login.phone = shrdpref.getString(Constants.PHONE, "")
                login.city = shrdpref.getString(Constants.CITY, "")
                login.state = shrdpref.getString(Constants.STATE, "")
                login.address1 = shrdpref.getString(Constants.ADDRESS1, "")
                login.address2 = shrdpref.getString(Constants.ADDRESS2, "")
                login.referencePoint = shrdpref.getString(Constants.REFERENCE_POINT, "")
                login.shopName = shrdpref.getString(Constants.SHOP_NAME, "")
                login.profileImage = shrdpref.getString(Constants.PROFILE_IMAGE, "")
                login.country = shrdpref.getString(Constants.COUNTRY, "")
                login.latitude = shrdpref.getString(Constants.LATITUDE, "0.0")
                login.longitude = shrdpref.getString(Constants.LONGITUDE, "0.0")
                login.taxId = shrdpref.getString(Constants.TAX_ID, "")
            }

            loginModel

        } ?: LoginModel()

    }


}