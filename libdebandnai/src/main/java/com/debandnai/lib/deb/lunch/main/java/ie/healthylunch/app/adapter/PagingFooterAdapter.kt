package ie.healthylunch.app.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.R
import ie.healthylunch.app.databinding.LoadStateViewBinding
import retrofit2.HttpException

class PagingFooterAdapter (private val retry: () -> Unit
) : LoadStateAdapter<PagingFooterAdapter.LoadStateViewHolder>(){


    inner class LoadStateViewHolder(val binding: LoadStateViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        with(holder.binding){
            btReloading.isVisible = loadState !is LoadState.Loading
            tvErrorExplain.isVisible = loadState !is LoadState.Loading
            progressBar.isVisible = loadState is LoadState.Loading
            tvErrorTitle.visibility = View.GONE
            if (loadState is LoadState.Error){
                if(loadState.error is HttpException) {
                    tvErrorExplain.text = root.context.getText(R.string.server_error_msg)
                }else{
                    tvErrorExplain.text = root.context.getText(R.string.network_error_msg)
                }
            }

            btReloading.setOnClickListener {
                retry.invoke()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = LoadStateViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }
}