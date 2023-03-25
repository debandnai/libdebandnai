package ie.healthylunch.app.fragment

import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import ie.healthylunch.app.R
import ie.healthylunch.app.data.viewModel.ProductViewModel
import ie.healthylunch.app.databinding.FragmentOrderProcessDialogBinding
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.WalletViewActivity
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.STATUS_100
import ie.healthylunch.app.utils.Constants.Companion.STATUS_35
import ie.healthylunch.app.utils.Constants.Companion.STATUS_70
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FIVE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FOUR
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_SIX
import ie.healthylunch.app.utils.Constants.Companion.STATUS_THREE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.MethodClass


class OrderProcessFragmentDialog : DialogFragment(),View.OnClickListener {
    private lateinit var fragmentOrderProcessDialogBinding: FragmentOrderProcessDialogBinding
    private val productViewModel: ProductViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.OrderProcessFragmentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentOrderProcessDialogBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_process_dialog, container, false)
        fragmentOrderProcessDialogBinding.viewModel = productViewModel
        fragmentOrderProcessDialogBinding.lifecycleOwner = viewLifecycleOwner
        init()
        onViewClick()
        return fragmentOrderProcessDialogBinding.root
    }

    private fun onViewClick() {
        with(fragmentOrderProcessDialogBinding){
            btnViewMyOrder.setOnClickListener(this@OrderProcessFragmentDialog)
            btnOrderstatus.setOnClickListener(this@OrderProcessFragmentDialog)
            btnOrderstatus2.setOnClickListener(this@OrderProcessFragmentDialog)
            btnOrderstatus3.setOnClickListener(this@OrderProcessFragmentDialog)
        }
    }

    private fun setIntent() {
        context?.let {ctx->
            var intent:Intent ?=null
            intent?.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            when (productViewModel.orderStatus.value) {

                STATUS_ONE, STATUS_FOUR, STATUS_FIVE, STATUS_SIX -> {
                    intent=Intent(ctx, DashBoardActivity::class.java)
                    dismiss()
                }

                Constants.STATUS_THREE -> {
                    intent =Intent(ctx, WalletViewActivity::class.java)
                    intent.putExtra(Constants.FROM, Constants.PRODUCT)
                    dismiss()
                }
            }
            intent?.let {intent-> startActivity(intent)}
        }
    }


    fun init(){
        productViewModel.orderStatus.observe(viewLifecycleOwner){ status->
            if (status== STATUS_ZERO ) {
                MethodClass.rotedImage(fragmentOrderProcessDialogBinding.imgOrderSuccessStatus)
            }
            else if(status== STATUS_TWO ){
                Glide.with(this@OrderProcessFragmentDialog).load(R.drawable.order_unger_process).into(fragmentOrderProcessDialogBinding.imgOrderUnderProcessStatus)
            }
        }

    }

    override fun onClick(v: View?) {
       when(v?.id){
          R.id.btnOrderstatus->{
              setIntent()
          }
          R.id.btnViewMyOrder->{
             setIntent()
          }
           R.id.btnOrderstatus2->{
             setIntent()
          }
           R.id.btnOrderstatus3->{
             setIntent()
          }

       }
    }
}