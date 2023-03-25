package com.salonsolution.app.data.network

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.salonsolution.app.BuildConfig
import com.salonsolution.app.R
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.utils.UtilsCommon.appUpdateRequired
import com.salonsolution.app.utils.UtilsCommon.userLogout
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {

    fun getClient(): Retrofit.Builder{

        //custom interceptor
        val commonHeaderInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }

        //logger
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        //Okhttp Client
        val client = OkHttpClient.Builder().also { client ->
            client.readTimeout(60, TimeUnit.SECONDS)
            client.writeTimeout(60, TimeUnit.SECONDS)
            client.connectTimeout(60, TimeUnit.SECONDS)
            client.addInterceptor(commonHeaderInterceptor)
            if (BuildConfig.DEBUG) {
                client.addInterceptor(interceptor)
            }
        }.build()

        val gson = GsonBuilder()
            .setLenient()
            .serializeNulls()
            .setPrettyPrinting()
            .create()

        return Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
    }

    fun Fragment.handleApiError(isNetworkError: Boolean, errorCode: Int?, errorMessage: String?, isShowPopup:Boolean=true) {
        context?.let {
            handleError(it, isNetworkError, errorCode, errorMessage, isShowPopup)
        }
    }

    fun Activity.handleApiError(isNetworkError: Boolean, errorCode: Int?, errorMessage: String?, isShowPopup:Boolean=true) {
        handleError(this, isNetworkError, errorCode, errorMessage, isShowPopup)
    }

    private fun handleError(
        context: Context,
        isNetworkError: Boolean,
        errorCode: Int?,
        errorMessage: String?,
        isShowPopup:Boolean,
    ) {

        if(isNetworkError) {
            // network error
           // ErrorPopUp(context).showMessageDialog(context.getString(R.string.network_error))
        }else{
            when (errorCode) {
                400 -> {
                    if(isShowPopup)
                        ErrorPopUp(context).showMessageDialog(errorMessage)
                }
                401 -> {
                    //session logout
                    context.userLogout()
                }
                403 -> {
                    //App Update Required
                    context.appUpdateRequired()
                }
                417 -> {
                    //session logout
                    context.userLogout()
                }
                else -> {
                    ErrorPopUp(context).showMessageDialog(context.getString(R.string.something_went_wrong))
                }
            }
        }
    }

}