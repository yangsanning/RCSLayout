package wot.view.rcslayout

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet

/**
 * @Sub RCSLayout
 * @Description RCSLayout 属性管理
 * @Author Wot.Yang
 * @CreateDate 2022/10/16
 * @Organization: Wot
 */
class RCSHelper {

    /**
     * 是否圆形
     */
    var mIsCircle = false

    /**
     * 圆角
     */
    var mCorner = 0f
    var mCornerLeftTop = 0f
    var mCornerTopRight = 0f
    var mCornerRightBottom = 0f
    var mCornerBottomLeft = 0f

    /**
     * 阴影大小
     */
    var mShadowSize = 0f
    var mShadowSizeLeft = 0f
    var mShadowSizeTop = 0f
    var mShadowSizeRight = 0f
    var mShadowSizeBottom = 0f

    private var shadowColorChanged = true

    /**
     * 阴影颜色
     */
    var mShadowColor: Int = 0x44000000
        set(value) {
            if (value == field)
                return
            field = value
            shadowColorChanged = true
        }

    var mClip = 0f
    var mClipLeft = 0f
    var mClipTop = 0f
    var mClipRight = 0f
    var mClipBottom = 0f

    val mPaint = Paint()

    /**
     *  top-left, top-right, bottom-right, bottom-left
     */
    var radii = FloatArray(8)

    var mRealShadowSize = 0f

    /**
     * 阴影Bitmap
     */
    var mShadowBitmap: Bitmap? = null

    /**
     * 阴影区域
     */
    var mShadowPath = Path()

    /**
     * View区域
     */
    var mClipPath = Path()

    var mRectClip = RectF()
    var mRectShadow = RectF()
    var mRect = RectF()
    var mViewWiVdth = 0
    var mViewHeight = 0

    init {
        mPaint.isAntiAlias = true
    }

    fun initAttr(ctx: Context?, attr: AttributeSet?) {
        if (ctx == null || attr == null)
            return
        val array = ctx.obtainStyledAttributes(attr, R.styleable.RCSAttrs)
        try {
            array.apply {
                mCorner = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_corner, 0).toFloat()
                val corner = mCorner.toInt()
                mCornerLeftTop = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_cornerTopLeft, corner).toFloat()
                mCornerTopRight = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_cornerTopRight, corner).toFloat()
                mCornerRightBottom = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_cornerBottomRight, corner).toFloat()
                mCornerBottomLeft = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_cornerBottomLeft, corner).toFloat()

                mIsCircle = getBoolean(R.styleable.RCSAttrs_rcs_circle, mIsCircle)

                mShadowColor = getColor(R.styleable.RCSAttrs_rcs_shadowColor, mShadowColor)
                mShadowSize = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_shadowSize, 0).toFloat()
                val shadowSize = mShadowSize.toInt()
                mShadowSizeLeft = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_shadowSizeLeft, shadowSize).toFloat()
                mShadowSizeTop = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_shadowSizeTop, shadowSize).toFloat()
                mShadowSizeRight = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_shadowSizeRight, shadowSize).toFloat()
                mShadowSizeBottom = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_shadowSizeBottom, shadowSize).toFloat()

                mClip = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_clip, 0).toFloat()
                val clip = mClip.toInt()
                mClipLeft = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_clipLeft, clip).toFloat()
                mClipTop = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_clipTop, clip).toFloat()
                mClipRight = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_clipRight, clip).toFloat()
                mClipBottom = getDimensionPixelSize(R.styleable.RCSAttrs_rcs_clipBottom, clip).toFloat()
            }
            array.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var canvas: Canvas? = null

    /**
     * 创建阴影
     */
    private fun createShader() {
        if (mRealShadowSize <= 0f)
            return
        if (!shadowColorChanged)
            return
        shadowColorChanged = false
        if (mShadowBitmap == null || canvas == null) {
            canvas = Canvas()
            mShadowBitmap = Bitmap.createBitmap(mViewWiVdth, mViewHeight, Bitmap.Config.ARGB_8888)
            canvas?.setBitmap(mShadowBitmap)
        }
        mPaint.color = Color.TRANSPARENT
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        mPaint.setShadowLayer(mRealShadowSize, 0f, 0f, mShadowColor)
        canvas?.drawPath(mShadowPath, mPaint)
        mPaint.clearShadowLayer()
    }

    fun onSizeChange(w: Int, h: Int) {
        mViewWiVdth = w
        mViewHeight = h
        computePath()
        createShader()
    }

    private fun computePath() {
        radii[0] = mCornerLeftTop
        radii[1] = mCornerLeftTop

        radii[2] = mCornerTopRight
        radii[3] = mCornerTopRight

        radii[4] = mCornerRightBottom
        radii[5] = mCornerRightBottom

        radii[6] = mCornerBottomLeft
        radii[7] = mCornerBottomLeft

        mClipPath.reset()
        mShadowPath.reset()
        mRect.set(0f, 0f, 0f + mViewWiVdth, 0f + mViewHeight)
        if (mIsCircle) {
            val circleA = (mViewWiVdth - mShadowSize * 2).coerceAtMost(mViewHeight - mShadowSize * 2)
            val left = (mViewWiVdth - circleA) / 2f
            val top = (mViewHeight - circleA) / 2f
            val right = left + circleA
            val bottom = top + circleA
            mRectClip.set(left, top, right, bottom)
            mClipPath.addRoundRect(mRectClip, circleA / 2f, circleA / 2f, Path.Direction.CW)
            mRealShadowSize = mShadowSize
            mRectShadow.set(mRectClip)
            mShadowPath.addRoundRect(mRectShadow, circleA / 2f, circleA / 2f, Path.Direction.CW)
        } else {
            mRectClip.set(mClipLeft, mClipTop, mViewWiVdth - mClipRight, mViewHeight - mClipBottom)
            mClipPath.addRoundRect(mRectClip, radii, Path.Direction.CW)

            val max = max(mShadowSize, mShadowSizeLeft, mShadowSizeTop, mShadowSizeRight, mShadowSizeBottom)
            mRealShadowSize = max
            mRectShadow.set(
                max - mShadowSizeLeft + mClipLeft,
                max - mShadowSizeTop + mClipTop,
                mViewWiVdth - (max - mShadowSizeRight + mClipRight),
                mViewHeight - (max - mShadowSizeBottom + mClipBottom)
            )
            mShadowPath.addRoundRect(mRectShadow, radii, Path.Direction.CW)
        }
    }

    private fun max(vararg value: Float): Float {
        var max = value[0]
        value.forEach {
            if (it > max)
                max = it
        }
        return max
    }

    fun drawBefore(c: Canvas?, isEditMode: Boolean = false) {
        if (c == null)
            return
        c.save()
        if (isEditMode) {
            c.clipPath(mClipPath)
            return
        }
        c.clipPath(mClipPath)
    }

    fun drawAfter(c: Canvas?, isEditMode: Boolean = false) {
        if (c == null) return

        if (!isEditMode) {
            mClipPath.fillType = Path.FillType.INVERSE_WINDING
            mPaint.color = Color.WHITE
            mPaint.style = Paint.Style.FILL
            c.drawPath(mClipPath, mPaint)
            mClipPath.fillType = Path.FillType.WINDING
        }
        c.restore()
        if (mRealShadowSize > 0 && !isEditMode) {
            c.save()
            mClipPath.fillType = Path.FillType.WINDING
            clipOutPath(c, mClipPath)
            mPaint.color = Color.WHITE
            mPaint.style = Paint.Style.FILL
            mShadowBitmap?.let { c.drawBitmap(it, 0f, 0f, mPaint) }
            c.restore()
        }
    }

    private fun clipOutPath(c: Canvas, p: Path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            c.clipOutPath(p)
        } else {
            c.clipPath(p, Region.Op.DIFFERENCE)
        }
    }
}