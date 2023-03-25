package ie.healthylunch.app.data.model.cardListModel

import com.google.gson.annotations.SerializedName

data class CardListResponse(@SerializedName("response")
                            val response: Response)