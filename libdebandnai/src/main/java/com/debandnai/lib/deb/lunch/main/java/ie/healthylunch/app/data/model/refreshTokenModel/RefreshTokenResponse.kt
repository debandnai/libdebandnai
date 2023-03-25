package ie.healthylunch.app.data.model.refreshTokenModel

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(@SerializedName("response")
                                val response: Response)