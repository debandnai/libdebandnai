package ie.healthylunch.app.data.model.schoolListModel

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("county_id") val countyId: Int = 0,
    @SerializedName("school_name") val schoolName: String = "",
    @SerializedName("id") val id: Int = 0,
    @SerializedName("school_type") val schoolType: Int? = 0,
    var isSelected: Boolean = false
)
