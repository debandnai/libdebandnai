package ie.healthylunch.app.data.model.favorites

import com.google.gson.annotations.SerializedName

data class Raws(@SerializedName("data")
                val data: Data?=null,
                @SerializedName("success_message")
                val successMessage: String? = null,
                @SerializedName("publish")
                val publish: Publish?=null)