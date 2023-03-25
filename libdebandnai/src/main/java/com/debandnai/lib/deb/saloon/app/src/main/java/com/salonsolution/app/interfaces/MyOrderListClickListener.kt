package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.OrderList

interface MyOrderListClickListener {
    fun onViewDetailsClick(position: Int, orderList: OrderList)
    fun onBuyItAgainClick(position: Int, orderList: OrderList)
    fun onCancelOrderClick(position: Int, orderList: OrderList)
}