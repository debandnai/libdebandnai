package com.salonsolution.app.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.preferences.AppSettingsPref.Companion.STATUS_DONE

class ContextUtils(base: Context) : ContextWrapper(base) {

    companion object {

/*        fun updateLocale(c: Context, localeToSwitchTo: Locale): ContextWrapper {
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
            context = context.createConfigurationContext(configuration)

            return ContextUtils(context)
        }*/
    }

}