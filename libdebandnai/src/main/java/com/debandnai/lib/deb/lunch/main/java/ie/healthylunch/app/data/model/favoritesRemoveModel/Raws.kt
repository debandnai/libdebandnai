package ie.healthylunch.app.data.model.favoritesRemoveModel

import com.google.gson.annotations.SerializedName

data class Raws(@SerializedName("success_message")
                val successMessage: String? = null,
                @SerializedName("publish")
                val publish: Publish?=null
                )