package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.CartFoodList
import com.salonsolution.app.data.model.CartProductList
import com.salonsolution.app.data.model.CartServiceList

interface CartItemClickListener {
    fun onProductAddClick(position: Int, cartServiceList: CartServiceList)
    fun onFoodAddClick(position: Int, cartServiceList: CartServiceList)
    fun onServiceDeleteClick(position: Int, cartServiceList: CartServiceList)
    fun onProductDeleteClick(
        position: Int, productListPosition: Int,
        productList: CartProductList, cartServiceList: CartServiceList
    )
    fun onFoodDeleteClick(
        position: Int, foodListPosition: Int,
        foodList: CartFoodList, cartServiceList: CartServiceList
    )
    fun onFoodUpdateClick(
        position: Int, foodListPosition: Int,
        foodList: CartFoodList, cartServiceList: CartServiceList
    )
    fun onFoodRemovedClick(
        position: Int, foodListPosition: Int,
        foodList: CartFoodList, cartServiceList: CartServiceList
    )
}