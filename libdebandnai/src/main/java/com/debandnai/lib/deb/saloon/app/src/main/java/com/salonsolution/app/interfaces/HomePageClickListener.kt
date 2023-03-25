package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.BookedServiceList
import com.salonsolution.app.data.model.PopularCategoryList
import com.salonsolution.app.data.model.UpcomingOrderList

interface HomePageClickListener {
    fun onOrderItemClickListener(position:Int,upcomingOrderList: UpcomingOrderList)
    fun onServiceItemClickListener(position:Int,bookedServiceList: BookedServiceList)
    fun onCategoryItemClickListener(position:Int,popularCategoryList: PopularCategoryList)
}