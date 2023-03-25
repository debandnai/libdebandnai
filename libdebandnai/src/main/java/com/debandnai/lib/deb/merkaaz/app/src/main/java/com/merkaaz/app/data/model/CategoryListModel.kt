package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class CategoryListModel (
    @SerializedName("category_list")
    var categoryList: ArrayList<CategoryData> = ArrayList()
    )

data class CategoryData(

    @SerializedName("category_id") var categoryId: String? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("category_image") var categoryImage: String? = null,
    @SerializedName("sub_category"   ) var subCategory   : ArrayList<SubCategory> = arrayListOf()


)


data class SubCategory (

    @SerializedName("subcategory_id"    ) var subcategoryId    : String? = null,
    @SerializedName("subcategory_image" ) var subcategoryImage : String? = null,
    @SerializedName("subcategory_name"  ) var subcategoryName  : String? = null,
    var isSelected :Boolean= false
)