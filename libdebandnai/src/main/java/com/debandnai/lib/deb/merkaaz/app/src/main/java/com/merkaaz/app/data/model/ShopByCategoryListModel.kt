package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ShopByCategoryListModel(

    @SerializedName("total_count") var totalCount: Int? = null,
    @SerializedName("category_list") var categoryList: ArrayList<ShopByCategoryList> = arrayListOf()
)

data class ShopByCategoryList(

    @SerializedName("category_id") var categoryId: Int? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("category_image") var categoryImage: String? = null

)