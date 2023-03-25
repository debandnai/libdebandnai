package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.DetailsServiceList
import com.salonsolution.app.data.model.OrderList

interface OrderDetailsListClickListener {
    fun onReviewSubmitClick(position: Int, detailsServiceList: DetailsServiceList, reviewStar:Float, reviewComment:String)
}