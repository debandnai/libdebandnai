package com.merkaaz.app.data.model

import com.merkaaz.app.utils.Constants
import org.w3c.dom.NameList
import java.util.*
import kotlin.collections.ArrayList

data class FilterCategoryModel(
    val parentTitle:String?=null,
    var type:Int = Constants.FILTER_CATEGORY_TYPE,
    var childList : ArrayList<ChildList> = ArrayList(),
//    var subList: ArrayList<FilterProductNameByCategory> = ArrayList(),
    var isExpanded:Boolean = false
)