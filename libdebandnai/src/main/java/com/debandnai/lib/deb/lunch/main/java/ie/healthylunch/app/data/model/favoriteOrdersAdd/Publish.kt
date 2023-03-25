package ie.healthylunch.app.data.model.favoriteOrdersAdd

import com.google.gson.annotations.SerializedName

data class Publish(@SerializedName("developer")
                   val developer: String? = null,
                   @SerializedName("version")
                   val version: String? = null)