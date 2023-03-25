package ie.healthylunch.app.data.model.productListByMenuTemplateModel

import com.google.gson.annotations.SerializedName

data class ProductGroupItem(
    @SerializedName("product_group_id") val productGroupId: Int? = 0,
    @SerializedName("max_number_of_product") val maxNumberOfProduct: Int? = 0,
    @SerializedName("product") var product: List<ProductItem?>?,
    @SerializedName("validation_message") val validationMessage: String? = null,
    @SerializedName("product_group_icon") val productGroupIcon: String? = null,
    @SerializedName("product_group_icon_active") val productGroupIconActive: Int? = 0,
    @SerializedName("product_group_name") val productGroupName: String? = null,
    @SerializedName("product_group_icon_id") val productGroupIconId: Int? = 0,
    @SerializedName("validation_ifexists") val validationIfexists: String? = null,
    @SerializedName("map_menu_with_product_group_id") val mapMenuWithProductGroupId: Int? = 0,
    var selectedChildItemNumber: Int? = 0
)