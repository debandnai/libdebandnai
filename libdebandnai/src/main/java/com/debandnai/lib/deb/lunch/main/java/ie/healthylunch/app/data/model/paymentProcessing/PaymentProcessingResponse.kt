package ie.healthylunch.app.data.model.paymentProcessing

import com.google.gson.annotations.SerializedName

data class PaymentProcessingResponse(@SerializedName("response")
                                     val response: Response)