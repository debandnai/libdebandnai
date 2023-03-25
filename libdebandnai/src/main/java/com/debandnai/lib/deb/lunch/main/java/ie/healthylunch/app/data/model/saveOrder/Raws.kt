package ie.healthylunch.app.data.model.saveOrder

import com.google.gson.annotations.SerializedName

data class Raws(@SerializedName("data")
                val data: Data,
                @SerializedName("success_message")
                val successMessage: String = "",
                @SerializedName("publish")
                val publish: Publish)