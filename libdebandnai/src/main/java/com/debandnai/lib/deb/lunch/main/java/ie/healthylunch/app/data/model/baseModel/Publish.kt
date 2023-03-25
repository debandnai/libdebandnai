package ie.healthylunch.app.data.model.baseModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Publish(
    @SerializedName("developer") val developer: String = "",
    @SerializedName("version") val version: String = ""
) : Parcelable