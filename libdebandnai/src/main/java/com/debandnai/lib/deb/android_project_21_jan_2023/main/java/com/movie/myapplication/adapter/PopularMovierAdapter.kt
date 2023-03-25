package com.movie.myapplication.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.movie.myapplication.data.model.popularMovie.Results

import com.movie.myapplication.databinding.PopularMovieListBinding
import com.movie.myapplication.interface_package.AdapterItemClickListener


class PopularMovierAdapter(
    var movieList: ArrayList<Results>,
    val listener: AdapterItemClickListener
) :
    RecyclerView.Adapter<PopularMovierAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = PopularMovieListBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movieList[position])
    }

    class ViewHolder(val binding: PopularMovieListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(results: Results) = with(itemView) {
            binding.movieResult=results
            binding.executePendingBindings()
        }

    }

    override fun getItemCount(): Int = movieList?.size ?: 0
}