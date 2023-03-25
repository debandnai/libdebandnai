package ie.healthylunch.app.data.model.productListByMenuTemplateModel

import com.google.gson.annotations.SerializedName

data class ProductItem(
    @SerializedName("is_promoted") val isPromoted: Int? = 0,
    @SerializedName("friday_order") var fridayOrder: Int? = 0,
    @SerializedName("is_allergen") val isAllergen: Int? = 0,
    @SerializedName("thursday_order") var thursdayOrder: Int? = 0,
    @SerializedName("thursday_product_availabilitie_sort_order") val thursdayProductAvailabilitieSortOrder: Int? = 0,
    @SerializedName("additional_price") val additionalPrice: String? = null,
    @SerializedName("tuesday_order") var tuesdayOrder: Int? = 0,
    @SerializedName("thursday") val thursday: Int? = 0,
    @SerializedName("friday_product_availabilitie_sort_order") val fridayProductAvailabilitieSortOrder: Int? = 0,
    @SerializedName("product_name") val productName: String? = null,
    @SerializedName("product_sub_heading") val productSubHeading: String? = null,
    @SerializedName("xp_points") var xpPoints: Int? = 0,
    @SerializedName("wednesday_order") var wednesdayOrder: Int? = 0,
    @SerializedName("map_menu_with_product_id") val mapMenuWithProductId: Int? = 0,
    @SerializedName("monday_product_availabilitie_sort_order") val mondayProductAvailabilitieSortOrder: Int? = 0,
    @SerializedName("product_desc") val productDesc: String? = null,
    @SerializedName("tuesday_product_availabilitie_sort_order") val tuesdayProductAvailabilitieSortOrder: Int? = 0,
    @SerializedName("wednesday_product_availabilitie_sort_order") val wednesdayProductAvailabilitieSortOrder: Int? = 0,
    @SerializedName("tuesday") val tuesday: Int? = 0,
    @SerializedName("is_no_type") val isNoType: Int? = 0,
    @SerializedName("product_id") val productId: Int? = 0,
    @SerializedName("monday_order") var mondayOrder: Int? = 0,
    @SerializedName("wednesday") val wednesday: Int? = 0,
    @SerializedName("friday") val friday: Int? = 0,
    @SerializedName("monday") val monday: Int? = 0,
    @SerializedName("packaging_type") val packagingType: Int? = 0,
    @SerializedName("file_name") val fileName: String? = null,
    var isOrdered: Int? = 0,
    var isXpLayoutVisible: Boolean = false
)