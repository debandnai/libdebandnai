package ie.healthylunch.app.data.model.allergenListModel

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("is_mapped")
    var isMapped: Int = 0,
    @SerializedName("allergen_name")
    var allergenName: String? = ""
)