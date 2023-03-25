package ie.healthylunch.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder


class UserPreferences {


    companion object {

        //Shared Preference field used to save and retrieve JSON string
        var preferences: SharedPreferences? = null

        //Name of Shared Preference file
        const val PREFERENCES_FILE_NAME = "USER_SHARED_PREFERENCE"

        inline fun <reified T> getAsObject(context: Context?, key: String): T? {
            preferences = context?.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

            //We read JSON String which was saved.
            val value = preferences?.getString(key, null)

            //JSON String was found which means object can be read.
            //We convert this JSON String to model object. Parameter "c" (of
            //type Class < T >" is used to cast.

            return GsonBuilder().create().fromJson(value, T::class.java)
        }


        fun <T> saveAsObject(context: Context?, `object`: T, key: String) {
            try {
                preferences = context?.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
                //Convert object to JSON String.
                val jsonString = GsonBuilder().create().toJson(`object`)
                //Save that String in SharedPreferences
                preferences?.edit()?.putString(key, jsonString)?.apply()
            }catch (e:Exception){}
        }


        //Shared Preference field used to save and retrieve Firebase token string
        lateinit var firebasePreference: SharedPreferences

        // Name of the Shared Preference file
        private const val FIREBASE_PREF_FILE_NAME = "FIREBASE_PREFERENCE"

        fun saveFirebaseToken(context: Context, token: String) {
            firebasePreference =
                context.getSharedPreferences(FIREBASE_PREF_FILE_NAME, Context.MODE_PRIVATE)

            //save the firebase token
            firebasePreference.edit().putString("firebase_token", token).apply()
        }

        fun getFirebaseToken(context: Context): String {
            firebasePreference =
                context.getSharedPreferences(FIREBASE_PREF_FILE_NAME, Context.MODE_PRIVATE)

            return firebasePreference.getString("firebase_token", "").toString()
        }


    }

}