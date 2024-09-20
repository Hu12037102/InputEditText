package com.huxiaobai.inputedit.weight

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.huxiaobai.inputedit.R


/**
 * 作者: 胡庆岭
 * 创建时间: 2022/4/27 16:06
 * 更新时间: 2022/4/27 16:06
 * 描述:
 */
class InputEditTextView : ConstraintLayout {
    private var mOnTextInputTextCallback: OnTextInputTextCallback? = null
    fun setOnTextInputTextCallback(onTextInputTextCallback: OnTextInputTextCallback) {
        this.mOnTextInputTextCallback = onTextInputTextCallback
    }

    companion object {
        private const val DEFAULT_COUNT = 4
        private const val TAG = "InputEditTextView"
        private const val TEXT_SIZE_NUMBER = 14
        private const val ITEM_SIZE = 50f

        @JvmStatic
        @ColorInt
        fun getColor(context: Context, @ColorRes resColor: Int): Int =
            ContextCompat.getColor(context, resColor)

        @JvmStatic
        fun dp2px(context: Context, value: Float): Int {
            val density = context.resources.displayMetrics.density
            return (density * value + 0.5f).toInt()
        }

        @JvmStatic
        fun getText(textView: TextView?): CharSequence {
            return if (textView == null) {
                ""
            } else {
                textView.text ?: ""
            }

        }

        @JvmStatic
        fun getScreenWidth(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private var mCount: Int = DEFAULT_COUNT
    private var mCheckBackgroundDrawable: Drawable? = checkBackgroundDrawable()
    private var mDefaultBackgroundDrawable: Drawable? = defaultBackgroundDrawable()
    private var mInputType: Int = InputType.TYPE_CLASS_TEXT
    private var mTextSize: Int = getDefaultTextSize()
    private var mEditText: AppCompatEditText? = null
    private var isFirst = true
    private var mItemWidth: Int = getDefaultItemSize()
    private var mItemHeight: Int = getDefaultItemSize()
    private val mIndicators = ArrayList<View>()

    @ColorInt
    private var mTextColor: Int = Color.BLACK

    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputEditTextView)
            mCount = typedArray.getInt(R.styleable.InputEditTextView_etv_count, DEFAULT_COUNT)
            mCheckBackgroundDrawable =
                typedArray.getDrawable(R.styleable.InputEditTextView_etv_check_background)
            mDefaultBackgroundDrawable =
                typedArray.getDrawable(R.styleable.InputEditTextView_etv_default_background)
            if (mDefaultBackgroundDrawable == null) {
                mDefaultBackgroundDrawable = defaultBackgroundDrawable()
            }
            mInputType = typedArray.getInt(
                R.styleable.InputEditTextView_android_inputType,
                InputType.TYPE_CLASS_TEXT
            )
            mTextSize = typedArray.getDimensionPixelSize(
                R.styleable.InputEditTextView_android_textSize,
                getDefaultTextSize()
            )
            mTextColor =
                typedArray.getColor(R.styleable.InputEditTextView_android_textColor, Color.BLACK)
            mItemWidth =
                typedArray.getDimensionPixelSize(
                    R.styleable.InputEditTextView_etv_item_width,
                    getDefaultItemSize()
                )
            mItemHeight =
                typedArray.getDimensionPixelSize(
                    R.styleable.InputEditTextView_etv_item_height,
                    getDefaultItemSize()
                )
            typedArray.recycle()
        }
    }

    private fun getDefaultTextSize(): Int = dp2px(context, TEXT_SIZE_NUMBER.toFloat())
    private fun getDefaultItemSize(): Int = dp2px(context, ITEM_SIZE)

    fun setCount(count: Int = DEFAULT_COUNT): InputEditTextView {
        this.mCount = count
        Log.w(TAG, "setCount--")
        return this
    }

    fun setBackground(
        defaultBackground: Drawable = defaultBackgroundDrawable(),
        checkBackground: Drawable = checkBackgroundDrawable()
    ): InputEditTextView {
        this.mDefaultBackgroundDrawable = defaultBackground
        this.mCheckBackgroundDrawable = checkBackground
        Log.w(TAG, "setBackground--")
        return this
    }

    fun setInputType(inputType: Int = InputType.TYPE_CLASS_TEXT): InputEditTextView {
        this.mInputType = inputType
        Log.w(TAG, "setInputType--")
        return this
    }


    private fun createChildView() {
        for (i in 0 until mCount) {
            val textView = AppCompatTextView(context)
            val textParams =
                ViewGroup.LayoutParams(mItemWidth, mItemHeight)
            textView.layoutParams = textParams
            textView.id = View.generateViewId()
            Log.w("createChildView--", "${textView.id}")
            textView.isCursorVisible = false
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize.toFloat())
            textView.tag = i
            textView.filters = arrayOf(InputFilter.LengthFilter(1))
            textView.setTextColor(mTextColor)
            textView.background = mDefaultBackgroundDrawable
            // textView.setBackgroundColor(Color.BLACK)
            textView.gravity = Gravity.CENTER
            textView.inputType = mInputType
            addView(textView)


        }
        for (i in 0 until mCount) {
            //  Log.w("createChildView--", "${getChildAt(i).id}")
            val set = ConstraintSet()
            set.clone(this)
            when (i) {
                0 -> {
                    set.connect(
                        getChildAt(i).id,
                        ConstraintSet.LEFT,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.LEFT
                    )
                    set.connect(
                        getChildAt(i).id,
                        ConstraintSet.RIGHT,
                        if (mCount == 1) {
                            ConstraintSet.PARENT_ID
                        } else {
                            getChildAt(1).id
                        },
                        ConstraintSet.LEFT
                    )
                    //  set.setHorizontalChainStyle(getChildAt(i).id,ConstraintSet.CHAIN_PACKED)
                    set.setHorizontalChainStyle(getChildAt(i).id, ConstraintSet.CHAIN_SPREAD)
                }

                mCount - 1 -> {
                    set.connect(
                        getChildAt(i).id,
                        ConstraintSet.LEFT,
                        getChildAt(i - 1).id,
                        ConstraintSet.RIGHT
                    )
                    set.connect(
                        getChildAt(i).id,
                        ConstraintSet.RIGHT,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.RIGHT
                    )
                }

                else -> {
                    set.connect(
                        getChildAt(i).id,
                        ConstraintSet.LEFT,
                        getChildAt(i - 1).id,
                        ConstraintSet.RIGHT
                    )
                    set.connect(
                        getChildAt(i).id,
                        ConstraintSet.RIGHT,
                        getChildAt(i + 1).id,
                        ConstraintSet.LEFT
                    )

                }

            }
            set.connect(
                getChildAt(i).id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            set.connect(
                getChildAt(i).id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
            set.applyTo(this)
        }

        mIndicators.clear()
        this.forEach {
            if (it is AppCompatTextView) {
                val indicator = View(context)
                indicator.layoutParams =
                    MarginLayoutParams(dp2px(context, 1f), mItemHeight - dp2px(context, 20f))
                indicator.background = GradientDrawable().also { drawable ->
                    drawable.setColor(Color.BLACK)
                    drawable.cornerRadius = dp2px(context, 10f).toFloat()
                }
                indicator.id = View.generateViewId()
                indicator.tag = it.tag
                this.addView(indicator)
                indicator.visibility = if (it.tag == 0) View.VISIBLE else View.GONE

                mIndicators.add(indicator)
                val animator = ObjectAnimator.ofFloat(indicator, "alpha", 1f, 0f)
                animator.setDuration(700) // Duration in milliseconds
                animator.repeatCount = ObjectAnimator.INFINITE
                animator.repeatMode = ObjectAnimator.REVERSE
                animator.interpolator = LinearInterpolator()
                animator.start()


                val set = ConstraintSet()
                set.clone(this)
                set.connect(indicator.id, ConstraintSet.START, it.id, ConstraintSet.START)
                set.connect(indicator.id, ConstraintSet.END, it.id, ConstraintSet.END)
                set.connect(indicator.id, ConstraintSet.TOP, it.id, ConstraintSet.TOP)
                set.connect(indicator.id, ConstraintSet.BOTTOM, it.id, ConstraintSet.BOTTOM)
                set.applyTo(this)


            }

        }



        if (mEditText == null) {
            mEditText = AppCompatEditText(context)
            mEditText?.apply {
                val editParams = ViewGroup.LayoutParams(
                    0,
                    0
                )
                id = View.generateViewId()
                layoutParams = editParams
                setBackgroundColor(Color.TRANSPARENT)
                isCursorVisible = false
                setTextColor(Color.WHITE)
                filters = arrayOf(InputFilter.LengthFilter(mCount), object : InputFilter {
                    override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                    ): CharSequence? {
                        return if (TextUtils.equals(" ", source)) {
                            ""
                        } else {
                            null
                        }
                    }

                })
                textSize = 0f
                inputType = mInputType
                addTextChangedListener(mTextChangeListener)
                isLongClickable = false
                addView(mEditText)

                ConstraintSet().let {
                    it.clone(this@InputEditTextView)
                    it.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                    it.connect(
                        id,
                        ConstraintSet.RIGHT,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.RIGHT
                    )
                    it.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                    it.connect(
                        id,
                        ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.BOTTOM
                    )
                    it.applyTo(this@InputEditTextView)
                }
            }

        }

        Log.w(TAG, "createView--$width----$height---$measuredWidth")
    }

    private val mTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.w("TextWatcher--", "onTextChanged--$s----$start----$before----$count")
            val text = getText(mEditText)
            val textLength = TextUtils.getTrimmedLength(text)
            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                if (childView is AppCompatTextView) {
                    if (childView.tag is Int) {
                        for (index in 0 until textLength) {
                            if (childView.tag == index) {
                                childView.text = "${text[index]}"
                            }
                        }
                    }
                    if (i >= textLength) {
                        childView.text = null
                        childView.background = mDefaultBackgroundDrawable
                    } else {
                        childView.background = mCheckBackgroundDrawable
                    }
                }
            }
            mIndicators.forEach {
                if (it.tag == text.length) {
                    it.visibility = VISIBLE
                } else {
                    it.visibility = GONE
                }

            }

            if (textLength == mCount) {
                mOnTextInputTextCallback?.onComplete(text)
            }

        }

        override fun afterTextChanged(s: Editable?) {
        }


    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.w(TAG, "onSizeChanged--$w----$h----$oldw----$oldh")
        if (isFirst) {
            post { createChildView() }
            isFirst = false
        }


    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        Log.w(TAG, "onWindowFocusChanged--$hasWindowFocus")

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            getSize(widthMeasureSpec, getScreenWidth(context)),
            getSize(heightMeasureSpec)
        )
        Log.w(TAG, "onMeasure--")
    }


    private fun getSize(measureSpec: Int, defaultSize: Int = getDefaultItemSize()): Int {
        val size = MeasureSpec.getSize(measureSpec)
        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.AT_MOST -> {
                size.coerceAtMost(defaultSize)
            }

            MeasureSpec.EXACTLY -> {
                size
            }

            else -> {
                defaultSize
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.w(TAG, "onDraw--")
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        Log.w(TAG, "onFocusChanged--$gainFocus--$direction")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.w(TAG, "onAttachedToWindow--")
    }

    private fun defaultBackgroundDrawable(): Drawable {
        val drawable = GradientDrawable()
        drawable.isFilterBitmap = true
        drawable.setStroke(dp2px(context, 1f), getColor(context, R.color.colorFF999999))
        drawable.setColor(Color.TRANSPARENT)
        drawable.cornerRadius = dp2px(context, 6f).toFloat()
        return drawable
    }

    private fun checkBackgroundDrawable(): Drawable {
        val drawable = GradientDrawable()
        drawable.isFilterBitmap = true
        drawable.setStroke(dp2px(context, 1f), getColor(context, R.color.colorFF333333))
        drawable.setColor(Color.TRANSPARENT)
        drawable.cornerRadius = dp2px(context, 6f).toFloat()
        return drawable
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mEditText?.removeTextChangedListener(mTextChangeListener)
        isFirst = true
    }

    interface OnTextInputTextCallback {
        fun onComplete(text: CharSequence)
    }
}