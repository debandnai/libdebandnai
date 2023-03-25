package com.salonsolution.app.data.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.ContextUtils
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppSettingsPref @Inject constructor (@ApplicationContext val context: Context) {
    companion object{
        private const val PREFERENCE_NAME = "app_data"
        private const val PREFERENCE_MODE = Context.MODE_PRIVATE
        private const val FCM_TOKEN = "fcm_token"
        private const val IS_FIRST_TIME = "isFirstTime"
        private const val KEY_LANGUAGE = "key_language"
        private const val REMEMBER_ME = "remember_me"
        private const val CURRENCY = "currency"
        const val FIRST_TIME_MIGRATION = "first_time_migration"
        const val STATUS_DONE = "status_done"
    }
    private var appData = context.getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE)

    /*............................set Get FCMToken data..................................*/

    fun setFCMToken(token: String) {
        val editor = appData.edit()
        editor.putString(FCM_TOKEN, token)
        editor.apply()
    }

    fun getFCMToken(): String {
        val deviceToken = appData.getString(FCM_TOKEN, "123")
        return deviceToken ?: "123"
    }

/*    fun setLanguage(langCode: String){
//        val editor = appData.edit()
//        editor.putString(KEY_LANGUAGE, langCode)
//        editor.apply()
        val localeList = LocaleListCompat.forLanguageTags(langCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun getLanguage(): String {
       return if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
            // Fetches the current Application Locale from the list
            AppCompatDelegate.getApplicationLocales()[0]?.language?:Constants.ENGLISH_CODE
        } else {
            // Fetches the default System Locale
            //Locale.getDefault().displayName
            Constants.ENGLISH_CODE
        }
        //return appData.getString(KEY_LANGUAGE, Constants.ENGLISH_CODE) ?: Constants.ENGLISH_CODE
    }

    fun getLanguageValue(): Int {
        return  if(SettingsUtils.getLanguage()== Constants.ENGLISH_CODE) 1 else 2
    }
    */
    fun getAlreadyStoredLanguageCode():String?{
        return appData.getString(KEY_LANGUAGE, Constants.ENGLISH_CODE)
    }

    fun isFirstTimeInstall(): Boolean {
        return appData?.getBoolean(IS_FIRST_TIME, true) ?: true
    }

    fun setFirstTimeToFalse() {
        val editor = appData?.edit()
        editor?.putBoolean(IS_FIRST_TIME, false)
        editor?.apply()
    }
    fun setRememberMe(email: String?){
        val editor = appData?.edit()
        editor?.putString(REMEMBER_ME, email)
        editor?.apply()
    }
    fun getRememberMe():String {
        return appData.getString(REMEMBER_ME, "") ?: ""
    }

    fun setCurrency(value: Int){
        val editor = appData.edit()
        editor.putInt(CURRENCY, value)
        editor.apply()
    }

    fun getCurrency(): Int {
       // 1-> USD, 2-> KZ
        return appData.getInt(CURRENCY, 1)
    }

    fun putLocaleMigrationStatus(value: String) {
        val editor = appData.edit()
        editor.putString(FIRST_TIME_MIGRATION, value)
        editor.apply()
    }

    fun getLocaleMigrationStatus(): String? {
        return appData.getString(FIRST_TIME_MIGRATION, null)
    }

    fun clearAppData() {
        appData?.edit()?.clear()?.apply()
    }
}