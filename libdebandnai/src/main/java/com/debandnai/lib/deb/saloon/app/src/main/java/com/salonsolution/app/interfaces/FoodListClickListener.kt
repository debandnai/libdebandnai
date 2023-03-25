package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.FoodList

interface FoodListClickListener {
    fun onItemClick(position: Int, foodList: FoodList)
    fun onAddClick(position: Int, foodList: FoodList)
    fun onUpdateClick(position: Int, foodList: FoodList)
    fun onRemovedClick(position: Int, foodList: FoodList)
}