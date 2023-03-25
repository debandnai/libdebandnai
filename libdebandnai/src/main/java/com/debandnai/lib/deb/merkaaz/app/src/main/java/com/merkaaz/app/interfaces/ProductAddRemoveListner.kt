package com.merkaaz.app.interfaces

import com.merkaaz.app.data.model.VariationDataItem

interface ProductAddRemoveListner {
    fun onAdditem(item: VariationDataItem?,productID : String?,position : Int?)
    fun onUpdateitem(item: VariationDataItem?,productID : String?,position : Int?,qty : Int?)
    fun onRemoveitem(item: VariationDataItem?,position : Int?)
}