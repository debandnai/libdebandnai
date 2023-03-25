package com.merkaaz.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.merkaaz.app.data.model.CountryListModel
import com.merkaaz.app.databinding.CountryCodeListItemsBinding


class LoginSpinnerAdapter(
    var act: Activity,
    var countryList: ArrayList<CountryListModel>
) : ArrayAdapter<CountryListModel>(act, 0, countryList) {

    override fun getItem(position: Int): CountryListModel {
        return countryList[position]
    }

    override fun getCount(): Int {
        return countryList.size
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position,convertView,parent)!!
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position,convertView,parent)!!
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View? {
      val binding: CountryCodeListItemsBinding =  CountryCodeListItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            )

        binding.spinner = countryList[position]
        binding.executePendingBindings()
        return binding.root
    }

}