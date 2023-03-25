package com.merkaaz.app.data.model

import androidx.lifecycle.MutableLiveData
import com.google.gson.annotations.SerializedName

data class ProductDetailsModel(

    @SerializedName("product_attr")
    val productAttrList: List<ProductAttrItem?>?,
    @SerializedName("images")
    val imagesList: List<ImagesItem?>?,
    @SerializedName("related_product")
    val relatedProductList: ArrayList<RelatedProductItem?>? = ArrayList(),
    @SerializedName("product_id")
    val productId: String? = null,

    @SerializedName("parent_cat_id") var parentCatId: String? = null,

    @SerializedName("short_desc")
    val shortDesc: String? = null,
    @SerializedName("product_name")
    val productName: String? = null,
    @SerializedName("subcat_name")
    val subCatName: String? = null,
    @SerializedName("brand_name")
    val brandName: String? = null,
    @SerializedName("variation")
    val variationList: List<VariationDataItem?>?,
    @SerializedName("long_desc")
    val longDesc: String? = null
)

data class ProductAttrItem(
    @SerializedName("attr_name")
    val attrName: String? = null,
    @SerializedName("attr_val")
    val attrVal: String? = null
)

data class ImagesItem(
    @SerializedName("image_link")
    val imageLink: String? = null,
    @SerializedName("is_default")
    val isDefault: String? = null
)

data class RelatedProductItem(
    @SerializedName("short_description")
    val shortDescription: String? = null,
    @SerializedName("product_id")
    val productId: String? = null,
    @SerializedName("long_description")
    val longDescription: String? = null,
    @SerializedName("product_name")
    val productName: String? = null,
    @SerializedName("subcategory_id")
    val subCategoryId: String? = null,
    @SerializedName("subcategory_name")
    val subCategoryName: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("variation")
    val variationDataList: ArrayList<VariationDataItem> = ArrayList(),
    var selectedVariationDataItem: VariationDataItem? = VariationDataItem(),
    var change_data : MutableLiveData<VariationDataItem> = MutableLiveData()
)

