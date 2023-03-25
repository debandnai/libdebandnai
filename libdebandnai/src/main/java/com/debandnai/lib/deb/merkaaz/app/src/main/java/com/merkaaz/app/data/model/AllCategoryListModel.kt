package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName

data class AllCategoryListModel(
    @SerializedName("sub_category") var subCategoryList: ArrayList<SubCategory> = arrayListOf(),
    @SerializedName("total_count") var totalCount: Int? = null
)


/*data class SubCategoryList(

    @SerializedName("category_id") var categoryId: Int? = null,
    @SerializedName("subcategory_id") var subcategoryId: Int? = null,
    @SerializedName("subcategory_image") var subcategoryImage: String? = null,
    @SerializedName("subcategory_name") var subcategoryName: String? = null,
    var isSelected: Boolean = false
)*/

