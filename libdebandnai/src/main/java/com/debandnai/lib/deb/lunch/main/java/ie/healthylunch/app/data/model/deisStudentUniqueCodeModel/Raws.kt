package ie.healthylunch.app.data.model.deisStudentUniqueCodeModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ie.healthylunch.app.data.model.baseModel.Publish
import kotlinx.parcelize.Parcelize

@Parcelize
data class Raws(
    @SerializedName("data") val data: Data,
    @SerializedName("success_message") val successMessage: String = "",
    @SerializedName("publish") val publish: Publish
) : Parcelable