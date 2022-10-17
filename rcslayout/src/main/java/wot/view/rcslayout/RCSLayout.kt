package wot.view.rcslayout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * @Sub RCSLayout
 * @Description 圆角+阴影+ConstraintLayout
 * @Author Wot.Yang
 * @CreateDate 2022/10/16
 * @Organization: Wot
 */
class RCSLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr){

    private val mRCSHelper = RCSHelper()

    init {
        mRCSHelper.initAttr(context, attrs)
    }

    private var isDrawled = false

    override fun draw(canvas: Canvas?) {
        isDrawled = true
        mRCSHelper.drawBefore(canvas, isInEditMode)
        super.draw(canvas)
        mRCSHelper.drawAfter(canvas, isInEditMode)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (isDrawled)
            super.dispatchDraw(canvas)
        else {
            mRCSHelper.drawBefore(canvas, isInEditMode)
            super.dispatchDraw(canvas)
            mRCSHelper.drawAfter(canvas, isInEditMode)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRCSHelper.onSizeChange(w, h)
    }
}