package ie.healthylunch.app.data.model.countryModel

import com.google.gson.annotations.SerializedName

data class DataItem(@SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("county_name")
                    val countyName: String = "") {

    var isSelected: Boolean=false
}