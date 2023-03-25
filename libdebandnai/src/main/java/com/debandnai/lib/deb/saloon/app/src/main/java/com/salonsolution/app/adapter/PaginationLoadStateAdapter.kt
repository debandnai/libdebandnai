package com.salonsolution.app.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salonsolution.app.R
import com.salonsolution.app.databinding.PaginationLoadStateViewBinding
import retrofit2.HttpException

class PaginationLoadStateAdapter (private val retry: () -> Unit
) : LoadStateAdapter<PaginationLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(val binding: PaginationLoadStateViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        with(holder.binding) {
            Log.d("tag", "onBindViewHolder : StickerPackLoadStateAdapter..")

            progressBar.isVisible = loadState is LoadState.Loading

            if (loadState is LoadState.Error) {
                if (loadState.error is HttpException) {
                    Log.d("tag", "onBindViewHolder : HttpException..")
                    Toast.makeText(holder.binding.root.context,root.context.getText(R.string.something_went_wrong),Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("tag", "onBindViewHolder : Network error..")
                   // nothing to do
                }
            }

            // for reload invoke retry
            //retry.invoke()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding: PaginationLoadStateViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.pagination_load_state_view,
            parent,
            false
        )

        return LoadStateViewHolder(binding)
    }
}