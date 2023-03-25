package com.salonsolution.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.salonsolution.app.R
import com.salonsolution.app.data.model.CountryList
import com.salonsolution.app.data.model.CountryListModel

class CustomArrayAdapter(context: Context, @LayoutRes resource: Int, var items: ArrayList<CountryList>) : ArrayAdapter<CountryList>(context, resource, items){
    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View{
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_layout, parent, false)
        }
        (view?.findViewById(R.id.title) as TextView).text = items[position].countryCode
        return view
    }
}