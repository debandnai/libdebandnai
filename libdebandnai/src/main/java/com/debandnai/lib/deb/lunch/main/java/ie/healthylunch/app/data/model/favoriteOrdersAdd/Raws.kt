package ie.healthylunch.app.data.model.favoriteOrdersAdd

import com.google.gson.annotations.SerializedName

data class Raws(@SerializedName("data")
                val data: Data,
                @SerializedName("success_message")
                val successMessage: String? = null,
                @SerializedName("publish")
                val publish: Publish?=null)