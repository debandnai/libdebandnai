package com.salonsolution.app.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ServiceDetailsModel(
    @SerializedName("service_id") var serviceId: Int? = null,
    @SerializedName("category_id") var categoryId: Int? = null,
    @SerializedName("category_name") var categoryName: String? = null,
    @SerializedName("service_name") var serviceName: String? = null,
    @SerializedName("service_desc") var serviceDesc: String? = null,
    @SerializedName("service_term") var serviceTerm: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("time_hr") var timeHr: String? = null,
    @SerializedName("images") var images: ArrayList<ServiceImages> = arrayListOf()

)

@Parcelize
data class ServiceImages(

    @SerializedName("image_id") var imageId: Int? = null,
    @SerializedName("image_link") var imageLink: String? = null,
    @SerializedName("is_default") var isDefault: String? = null

): Parcelable

data class Description(
    var title: String?=null,
    var description:String?=null
)