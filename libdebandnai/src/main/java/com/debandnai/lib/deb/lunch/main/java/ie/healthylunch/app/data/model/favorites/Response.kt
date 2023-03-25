package ie.healthylunch.app.data.model.favorites

import com.google.gson.annotations.SerializedName

data class Response(@SerializedName("raws")
                    val raws: Raws)