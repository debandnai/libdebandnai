package ie.healthylunch.app.data.model.favorites

import com.google.gson.annotations.SerializedName

data class FavoritesResponse(@SerializedName("response")
                             val response: Response)