package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class LatestReviewListModel(
    @SerializedName("latest_review_list") var latestReviewList: ArrayList<LatestReviewList> = arrayListOf()
)

data class LatestReviewList(
    @SerializedName("customer_name") var customerName: String? = null,
    @SerializedName("customer_image") var customerImage: String? = null,
    @SerializedName("review_point") var reviewPoint: Int? = null,
    @SerializedName("review_text") var reviewText: String? = null,
    @SerializedName("review_date") var reviewDate: String? = null
)