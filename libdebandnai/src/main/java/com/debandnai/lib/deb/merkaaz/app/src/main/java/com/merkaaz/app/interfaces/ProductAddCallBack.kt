package com.merkaaz.app.interfaces

import com.merkaaz.app.data.model.FeaturedData

interface ProductAddCallBack {
    fun productAdded(featuredData: FeaturedData)
}