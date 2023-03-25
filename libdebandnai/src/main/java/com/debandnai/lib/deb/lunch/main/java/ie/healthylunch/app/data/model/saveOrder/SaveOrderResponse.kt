package ie.healthylunch.app.data.model.saveOrder

import com.google.gson.annotations.SerializedName

data class SaveOrderResponse(@SerializedName("response")
                             val response: Response)