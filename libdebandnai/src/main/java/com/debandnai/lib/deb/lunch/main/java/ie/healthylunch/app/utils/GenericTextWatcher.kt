package ie.healthylunch.app.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View

class GenericTextWatcher(
    private var view: View,
    private var textWatcherListener: TextWatcherListener
) : TextWatcher {


    //fun setCallBack()

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        textWatcherListener.beforeTextChanged(s, start, count, after, view)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        textWatcherListener.onTextChanged(s, start, before, count, view)
    }

    override fun afterTextChanged(s: Editable?) {
        textWatcherListener.afterTextChanged(s, view)
    }


}