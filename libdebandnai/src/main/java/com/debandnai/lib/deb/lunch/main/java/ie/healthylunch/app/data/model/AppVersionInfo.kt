package ie.healthylunch.app.data.model

import com.google.gson.annotations.SerializedName

data class AppVersionInfo(@SerializedName("version")
                          val version: String? = null,
                          @SerializedName("build")
                          val build: String ?= null)