package ie.healthylunch.app.data.model.paymentProcessing

import com.google.gson.annotations.SerializedName

data class Raws(@SerializedName("data")
                val data: Data,
                @SerializedName("success_message")
                val success_message: String? = null,
                @SerializedName("publish")
                val publish: Publish)