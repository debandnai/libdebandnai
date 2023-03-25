package com.merkaaz.app.data.model

import androidx.lifecycle.MutableLiveData
import com.google.gson.annotations.SerializedName


data class FeatureProductListModel(
    @SerializedName("total_count") var total_count: Int? = null,

    @SerializedName("featured_product")
    var featureList: ArrayList<FeaturedData> = ArrayList(),

    @SerializedName("bestseller_product")
    var bestSellerList: ArrayList<FeaturedData> = ArrayList(),

    @SerializedName("product_list")
    var productList: ArrayList<FeaturedData> = ArrayList(),

    @SerializedName("category_list")
    var category_list: ArrayList<FeaturedData> = ArrayList(),

    @SerializedName("category_id") var categoryId: Int? = 0,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("parent_cat_id") var parentCatId: String? = null

)

data class FeaturedData(

    @SerializedName("product_id") var productId: String? = null,
    @SerializedName("product_image") var image: String? = null,
    @SerializedName("product_name") var product_name: String? = null,
    @SerializedName("subcategory_id") var subcategory_id: String? = null,
    @SerializedName("subcategory_name") var subcategory_name: String? = null,
    @SerializedName("brand_name") var brand_name: String? = null,
    @SerializedName("variation") var variationDataList: ArrayList<VariationDataItem> = ArrayList(),
    var variationData: VariationDataItem = VariationDataItem(),
    var change_data: MutableLiveData<VariationDataItem> = MutableLiveData()
)
