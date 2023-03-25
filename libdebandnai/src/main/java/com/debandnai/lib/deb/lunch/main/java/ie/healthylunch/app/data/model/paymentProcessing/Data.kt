package ie.healthylunch.app.data.model.paymentProcessing

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("status")
                val status: Int = 0)