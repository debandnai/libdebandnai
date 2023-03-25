package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class ServicesListModel(
    @SerializedName("cat_id") var catId: Int? = null,
    @SerializedName("service_list") var servicesList: ArrayList<ServicesList> = arrayListOf(),
    @SerializedName("total_count") var totalCount: Int? = null
)


data class ServicesList(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("service_name") var serviceName: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("hour_min") var hourMin: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("description") var description: String? = null

)

data class PagingMetadata(
    val totalElements: Int,
    val currentPage: Int,
    val totalPages: Int
)