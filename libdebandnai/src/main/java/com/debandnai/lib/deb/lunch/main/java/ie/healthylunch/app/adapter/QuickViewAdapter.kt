package ie.healthylunch.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.data.model.quickViewOrderDayModel.StudentListItem
import ie.healthylunch.app.databinding.QuickViewListBinding
import ie.healthylunch.app.ui.QuickViewForStudentActivity
import ie.healthylunch.app.utils.AdapterItemOnclickListener

class QuickViewAdapter(
    var studentList: List<StudentListItem>,
    var listener: AdapterItemOnclickListener,
    var activity: QuickViewForStudentActivity
) : RecyclerView.Adapter<QuickViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuickViewAdapter.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = QuickViewListBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }


    override fun getItemCount(): Int = studentList.size
    inner class ViewHolder(val binding: QuickViewListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudentListItem) {
            binding.studentList = item
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(studentList[position])
        holder.binding.studentDetailsRl.setOnClickListener {
            listener.onAdapterItemClick(
                studentList,
                position,
                "QuickView"
            )

        }
    }

}