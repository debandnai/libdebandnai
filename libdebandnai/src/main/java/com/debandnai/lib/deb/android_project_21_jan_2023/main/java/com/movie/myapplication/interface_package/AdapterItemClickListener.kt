package com.movie.myapplication.interface_package

import android.view.View

interface AdapterItemClickListener {
    fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, clickView: View?, tag: String)

}