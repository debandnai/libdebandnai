package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class PopularCategoryListModel(
    @SerializedName("popular_category_list") var popularCategoryList: ArrayList<PopularCategoryList> = arrayListOf()
)

data class PopularCategoryList(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("category_image") var categoryImage: String? = null
)