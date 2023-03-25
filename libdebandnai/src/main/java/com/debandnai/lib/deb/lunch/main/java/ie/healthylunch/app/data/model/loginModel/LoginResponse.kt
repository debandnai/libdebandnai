package ie.healthylunch.app.data.model.loginModel

import com.google.gson.annotations.SerializedName

data class LoginResponse(@SerializedName("response")
                         val response: Response)