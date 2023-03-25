package com.salonsolution.app.data.model

import com.google.gson.annotations.SerializedName

data class StaffReviewListModel(
    @SerializedName("total_review") var totalReview: Int? = null,
    @SerializedName("review_list") var reviewList: ArrayList<ReviewList> = arrayListOf()
)

data class ReviewList(

    @SerializedName("customer_name") var customerName: String? = null,
    @SerializedName("image_link") var imageLink: String? = null,
    @SerializedName("review_date") var reviewDate: String? = null,
    @SerializedName("review_point") var reviewPoint: String? = null,
    @SerializedName("review_data") var reviewData: String? = null

)