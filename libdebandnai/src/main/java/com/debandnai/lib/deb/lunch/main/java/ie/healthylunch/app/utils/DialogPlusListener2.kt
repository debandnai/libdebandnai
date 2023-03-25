package ie.healthylunch.app.utils

import android.app.Activity
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.dialogplus.DialogPlus

interface DialogPlusListener2 {
    fun setBottomDialogListener2(
        activity: Activity,
        dialogPlus: DialogPlus,
        recyclerView: RecyclerView,
        tag: String
    )
}