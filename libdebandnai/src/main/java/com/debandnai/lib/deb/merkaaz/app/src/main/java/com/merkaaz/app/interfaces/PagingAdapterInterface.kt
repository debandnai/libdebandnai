package com.merkaaz.app.interfaces

import com.merkaaz.app.data.model.FeaturedData

interface PagingAdapterInterface {
    fun onAdapterItemClick(item: FeaturedData?)
}