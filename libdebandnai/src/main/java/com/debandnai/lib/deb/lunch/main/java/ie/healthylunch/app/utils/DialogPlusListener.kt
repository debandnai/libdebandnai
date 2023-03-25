package ie.healthylunch.app.utils

import android.app.Activity
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.dialogplus.DialogPlus

interface DialogPlusListener {
    fun setBottomDialogListener(
        activity: Activity,
        dialogPlus: DialogPlus,
        recyclerView: RecyclerView,
        noDataFoundTv: TextView,
        tag: String
    )
}