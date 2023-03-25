package com.merkaaz.app.data.model

data class FilterProductNameByCategory(
    val productNameByFilter:String,
    var isChecked:Boolean=false,
    var parentPosition: Int =-1,
    var childPosition: Int =-1

)