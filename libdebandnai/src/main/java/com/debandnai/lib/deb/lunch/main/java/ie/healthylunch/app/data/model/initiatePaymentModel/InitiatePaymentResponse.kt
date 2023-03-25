package ie.healthylunch.app.data.model.initiatePaymentModel

import com.google.gson.annotations.SerializedName

data class InitiatePaymentResponse(@SerializedName("response")
                                   val response: Response)