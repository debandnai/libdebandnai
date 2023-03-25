package com.salonsolution.app.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.salonsolution.app.data.preferences.AppSettingsPref

object SettingsUtils {
    fun setAppTheme(themeValue: String?){

        when (themeValue) {
            "1" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "2" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun runLocaleMigration(context: Context) {
        /* NOTE: If you were handling the locale storage on you own earlier, you will need to add a
    one time migration for switching this storage from a custom way to the AndroidX storage.
    This can be done in the following manner. Lets say earlier the locale preference was
    stored in a SharedPreference */

        val appSettingsPref = AppSettingsPref(context)
        // Check if the migration has already been done or not
        if (appSettingsPref.getLocaleMigrationStatus() != AppSettingsPref.STATUS_DONE) {
            // Fetch the selected language from wherever it was stored. In this case its SharedPref
            appSettingsPref.getAlreadyStoredLanguageCode()?.let {
                // Set this locale using the AndroidX library that will handle the storage itself
                val localeList = LocaleListCompat.forLanguageTags(it)
                AppCompatDelegate.setApplicationLocales(localeList)
                // Set the migration flag to ensure that this is executed only once
                appSettingsPref.putLocaleMigrationStatus(AppSettingsPref.STATUS_DONE)
            }
        }
    }

    fun setLanguage(langCode: String){
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
    }

    fun getLanguageValue(): Int {
        return  if(SettingsUtils.getLanguage()== Constants.ENGLISH_CODE) 1 else 2
    }
}