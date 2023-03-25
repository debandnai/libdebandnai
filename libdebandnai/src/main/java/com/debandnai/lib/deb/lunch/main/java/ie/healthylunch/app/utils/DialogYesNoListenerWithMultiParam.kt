package ie.healthylunch.app.utils

import android.app.Activity
import android.app.Dialog

interface DialogYesNoListenerWithMultiParam {
    fun yesOnClickWithMultiParam(dialog: Dialog, activity: Activity,prm1:Any,prm2:Any,prm3:Any,prm4:Any)
    fun noOnClickWithMultiParam(dialog: Dialog, activity: Activity,prm1:Any,prm2:Any,prm3:Any,prm4:Any)

}