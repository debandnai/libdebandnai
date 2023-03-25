package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.ProductList

interface ProductListClickListener {
    fun onItemClick(position: Int, productList: ProductList)
    fun onAddClick(position: Int, productList: ProductList)
    fun goToCartClick(position: Int, productList: ProductList)
}