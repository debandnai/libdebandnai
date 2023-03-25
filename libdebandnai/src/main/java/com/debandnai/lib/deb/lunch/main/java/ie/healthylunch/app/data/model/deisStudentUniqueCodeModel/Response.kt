package ie.healthylunch.app.data.model.deisStudentUniqueCodeModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Response(
    @SerializedName("raws") val raws: Raws
) : Parcelable