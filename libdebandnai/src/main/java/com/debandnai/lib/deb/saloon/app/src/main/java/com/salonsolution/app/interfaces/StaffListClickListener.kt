package com.salonsolution.app.interfaces

import com.salonsolution.app.data.model.StaffList

interface StaffListClickListener {
    fun onItemClick(position: Int, staffList: StaffList)
}