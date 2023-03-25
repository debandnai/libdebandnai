package ie.healthylunch.app.data.model.logoutModel

import com.google.gson.annotations.SerializedName

data class LogoutResponse(@SerializedName("response")
                          val response: Response)