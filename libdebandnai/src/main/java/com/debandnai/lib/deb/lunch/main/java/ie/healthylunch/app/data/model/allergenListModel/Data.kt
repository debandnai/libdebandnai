package ie.healthylunch.app.data.model.allergenListModel

import com.google.gson.annotations.SerializedName
import ie.healthylunch.app.data.model.baseModel.Publish

data class Data(@SerializedName("allergen")
                val allergen: List<DataItem>?= null,
                @SerializedName("nutritional")
                val nutritional: List<DataItem>?= null,
                @SerializedName("cultural")
                val cultural: List<DataItem>?= null
                )