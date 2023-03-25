package com.salonsolution.app.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoriesModel(
    @SerializedName("category_list") var categoryListModel: ArrayList<CategoryListModel> = arrayListOf(),
    @SerializedName("total_count") var totalCount: Int? = null
): Parcelable

@Parcelize
data class CategoryListModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("image") var image: String? = null,
    var isSelected :Boolean= false
): Parcelable