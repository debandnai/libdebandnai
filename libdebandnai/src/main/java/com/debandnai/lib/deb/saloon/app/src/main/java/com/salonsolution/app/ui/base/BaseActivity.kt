package com.salonsolution.app.ui.base

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.utils.ContextUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
open class BaseActivity: AppCompatActivity() {
    private lateinit var oldPrefLocaleCode : String

    private lateinit var appSettingsPref: AppSettingsPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

//    override fun attachBaseContext(context: Context) {
        // get chosen language from shared preference
//        appSettingsPref = AppSettingsPref(context)
//        oldPrefLocaleCode = appSettingsPref.getLanguage()
//        val localeToSwitchTo = Locale(oldPrefLocaleCode)
//        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(context, localeToSwitchTo)
//        super.attachBaseContext(localeUpdatedContext)
//    }

    override fun onResume() {
//        val currentLocaleCode = appSettingsPref.getLanguage()
//        if(oldPrefLocaleCode != currentLocaleCode){
//            recreate() //locale is changed, restart the activty to update
//            oldPrefLocaleCode = currentLocaleCode
//        }
        super.onResume()
    }

}