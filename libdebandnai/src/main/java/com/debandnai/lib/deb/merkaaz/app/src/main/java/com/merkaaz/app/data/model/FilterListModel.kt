package com.merkaaz.app.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FilterListModel(

    @SerializedName("brand_list") var brandList: ArrayList<ChildList> = ArrayList(),
    @SerializedName("price_list") var priceList: ArrayList<ChildList> = ArrayList(),
    @SerializedName("discount_list") var discountList: ArrayList<ChildList> = ArrayList(),
    @SerializedName("size_list") var sizeList: ArrayList<ChildList> = ArrayList()

): Serializable

data class ChildList(
    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String,
    var isChecked:Boolean = false,
    var parentPosition: Int = -1,
    var childPosition: Int = -1

): Serializable