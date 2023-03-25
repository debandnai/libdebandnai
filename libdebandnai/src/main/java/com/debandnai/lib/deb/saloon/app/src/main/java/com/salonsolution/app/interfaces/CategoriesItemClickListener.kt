package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.CategoryListModel

interface CategoriesItemClickListener {
    fun onItemClick(position: Int, categoryListModel: CategoryListModel)
}