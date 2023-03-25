package ie.healthylunch.app.data.model.parentDetailsModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParentDetailsResponse(@SerializedName("response")
                                 val response: Response) : Parcelable