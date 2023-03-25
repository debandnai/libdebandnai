package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.ServicesList

interface ServiceListClickListener {
    fun onItemClick(position: Int, serviceList: ServicesList)
    fun onAddClick(position: Int, serviceList: ServicesList)
}