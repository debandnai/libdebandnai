package ie.healthylunch.app.utils.coverflow
import android.support.v4.view.LinkagePager
import androidx.viewpager.widget.ViewPager
import java.lang.IllegalArgumentException

class CoverFlow(builder: Builder) {

    private val viewPager: ViewPager
    private val linkagePager: LinkagePager
    private val scaleValue: Float
    private val pagerMargin: Float
    private val spaceSize: Float
    private val rotationY: Float

    init {
        if (builder == null) {
            throw IllegalArgumentException("A non-null CoverFlow.Builde must be provided")
        }
        viewPager = builder.viewPager
        linkagePager = builder.linkagePager
        scaleValue = builder.scaleValue
        pagerMargin = builder.pagerMargin
        spaceSize = builder.spaceSize
        rotationY = builder.rotationY


    }


    companion object {
        class Builder {
            internal lateinit var viewPager: ViewPager
            internal lateinit var linkagePager: LinkagePager
            internal var scaleValue = 0f
            internal var pagerMargin = 0f
            internal var spaceSize = 0f
            internal var rotationY = 0f


            fun with(viewPager: ViewPager): Builder {
                this.viewPager = viewPager
                return this

            }

            fun withLinkage(linkagePager: LinkagePager): Builder {
                this.linkagePager = linkagePager
                return this
            }

            fun scale(scaleValue: Float): Builder? {
                this.scaleValue = scaleValue
                return this
            }

            fun pagerMargin(pagerMargin: Float): Builder {
                this.pagerMargin = pagerMargin
                return this
            }

            fun spaceSize(spaceSize: Float): Builder {
                this.spaceSize = spaceSize
                return this
            }

            fun rotationY(rotationY: Float): Builder {
                this.rotationY = rotationY
                return this
            }

            fun build(): CoverFlow {
                return CoverFlow(this)
            }


        }
    }

}