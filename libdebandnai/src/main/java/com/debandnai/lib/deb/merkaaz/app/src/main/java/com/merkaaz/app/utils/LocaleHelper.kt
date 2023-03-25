package com.merkaaz.app.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.*


class LocaleHelper(base: Context) : ContextWrapper(base) {
    companion object {
//        private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

//    // the method is used to set the language at runtime
//    fun setLocale(context: Context, language: String): Context? {
//        persist(context, language)
//
//        // updating the language for devices above android nougat
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            updateResources(context, language)
//        } else updateResourcesLegacy(context, language)
//        // for devices having lower version of android os
//    }
//
//    private fun persist(context: Context, language: String) {
//        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val editor = preferences.edit()
//        editor.putString(SELECTED_LANGUAGE, language)
//        editor.apply()
//    }
//
//    // the method is used update the language of application by creating
//    // object of inbuilt Locale class and passing language argument to it
//    @TargetApi(Build.VERSION_CODES.N)
//    private fun updateResources(context: Context, language: String): Context? {
//        val locale = Locale(language)
//        Locale.setDefault(locale)
//        val configuration: Configuration = context.resources.configuration
//        configuration.setLocale(locale)
//        configuration.setLayoutDirection(locale)
//        return context.createConfigurationContext(configuration)
//    }
//
//
//    private fun updateResourcesLegacy(context: Context, language: String): Context? {
//        val locale = Locale(language)
//        Locale.setDefault(locale)
//        val resources: Resources = context.resources
//        val configuration: Configuration = resources.configuration
//        configuration.locale = locale
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            configuration.setLayoutDirection(locale)
//        }
//        resources.updateConfiguration(configuration, resources.displayMetrics)
//        return context
//    }

        fun setLocale(context: Context, language: String?): Context? {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val resources: Resources = context.resources
            val configuration = Configuration(resources.configuration)
            configuration.setLayoutDirection(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(locale)
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            } else {

                configuration.locale = locale
                configuration.setLocale(locale)
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                context.createConfigurationContext(configuration)
            } else {
                resources.updateConfiguration(configuration, resources.displayMetrics)
                context
            }
        }

//        fun onAttach(context: Context): Context? {
//            return setLocale(context, SharedPreff.getlanguage_code(context))
//        }

        fun updateLocale(c: Context, localeToSwitchTo: Locale): ContextWrapper {
            var context = c
            val resources: Resources = context.resources
            val configuration: Configuration = resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeList = LocaleList(localeToSwitchTo)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            } else {
                configuration.setLocale(localeToSwitchTo)
            }
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context = context.createConfigurationContext(configuration)
//            } else {
//                resources.updateConfiguration(configuration, resources.displayMetrics)
//            }

            return LocaleHelper(context)
        }

    }
}