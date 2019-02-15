package org.michaelbel.tjgram.modules.profile.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.utils.ViewUtil

class ExpandableView : LinearLayout {

    companion object {
        const val DEFAULT_ANIM_DURATION = 250

        private const val COLLAPSING = 0
        private const val EXPANDING = 1
    }

    private var title: String? = null

    private var containerView: ViewGroup? = null

    private var arrowIcon: AppCompatImageView? = null
    private var textViewTitle: TextView? = null

    private var innerViewRes: Int = 0

    private var card: FrameLayout? = null
    private var animDuration = DEFAULT_ANIM_DURATION.toLong()

    private var isExpanded = false

    private var isExpanding = false
    private var isCollapsing = false
    private var expandOnClick = false
    private var startExpanded = false

    private var previousHeight = 0

    private var innerView: LinearLayoutCompat? = null

    private var listener: OnExpandedListener? = null

    private val defaultClickListener = {
        if (isExpanded)
            collapse()
        else
            expand()
    }

    private val isMoving: Boolean get() = isExpanding || isCollapsing

    interface OnExpandedListener {
        fun onExpandChanged(v: View?, isExpanded: Boolean)
    }

    constructor(context: Context) : super(context) {
        initAttributes(context, null)
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(context, attrs)
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttributes(context, attrs)
        initView(context)
    }

    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.expandable_view, this)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableView)
        title = typedArray.getString(R.styleable.ExpandableView_title)
        innerViewRes = typedArray.getResourceId(R.styleable.ExpandableView_inner_view, View.NO_ID)
        expandOnClick = typedArray.getBoolean(R.styleable.ExpandableView_expandOnClick, true)
        animDuration = typedArray.getInteger(R.styleable.ExpandableView_animationDuration, DEFAULT_ANIM_DURATION).toLong()
        startExpanded = typedArray.getBoolean(R.styleable.ExpandableView_startExpanded, false)
        typedArray.recycle()
    }

    fun setInnerViewRes(@LayoutRes layoutId: Int) {
        innerViewRes = layoutId
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        arrowIcon = findViewById(R.id.arrow)
        arrowIcon!!.setImageDrawable(ViewUtil.getIcon(context, R.drawable.ic_chevron_down, R.color.icon_active))
        textViewTitle = findViewById(R.id.title)

        if (!TextUtils.isEmpty(title)) textViewTitle!!.text = title

        card = findViewById(R.id.expandLayout)

        setInnerView(innerViewRes)

        containerView = findViewById(R.id.viewContainer)

        if (startExpanded) {
            setAnimDuration(0)
            expand()
            setAnimDuration(animDuration)
        }

        if (expandOnClick) {
            card!!.setOnClickListener {
                if (isExpanded)
                    collapse()
                else
                    expand()
            }

            //arrowIcon.setOnClickListener(defaultClickListener);
        }
    }

    private fun expand() {
        val initialHeight = card!!.height

        if (!isMoving) {
            previousHeight = initialHeight
        }

        card!!.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = card!!.measuredHeight

        if (targetHeight - initialHeight != 0) {
            animateViews(initialHeight, targetHeight - initialHeight, EXPANDING)
        }
    }

    private fun collapse() {
        val initialHeight = card!!.measuredHeight

        if (initialHeight - previousHeight != 0) {
            animateViews(initialHeight,
                    initialHeight - previousHeight,
                    COLLAPSING)
        }
    }

    private fun animateViews(initialHeight: Int, distance: Int, animationType: Int) {
        val expandAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    isExpanding = false
                    isCollapsing = false

                    if (listener != null) {
                        if (animationType == EXPANDING) {
                            listener!!.onExpandChanged(card, true)
                        } else {
                            listener!!.onExpandChanged(card, false)
                        }
                    }
                }

                card!!.layoutParams.height = if (animationType == EXPANDING)
                    (initialHeight + distance * interpolatedTime).toInt()
                else
                    (initialHeight - distance * interpolatedTime).toInt()
                card!!.findViewById<View>(R.id.viewContainer).requestLayout()

                containerView!!.layoutParams.height = if (animationType == EXPANDING)
                    (initialHeight + distance * interpolatedTime).toInt()
                else
                    (initialHeight - distance * interpolatedTime).toInt()

            }

            override fun willChangeBounds(): Boolean = true
        }

        val arrowAnimation = if (animationType == EXPANDING)
            RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        else
            RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

        arrowAnimation.fillAfter = true

        arrowAnimation.duration = animDuration
        expandAnimation.duration = animDuration

        isExpanding = animationType == EXPANDING
        isCollapsing = animationType == COLLAPSING

        startAnimation(expandAnimation)
        arrowIcon!!.startAnimation(arrowAnimation)
        isExpanded = animationType == EXPANDING
    }

    fun setOnExpandedListener(listener: OnExpandedListener) {
        this.listener = listener
    }

    fun setTitle(title: String) {
        if (textViewTitle != null) textViewTitle!!.text = title
    }

    fun setTitle(resId: Int) {
        if (textViewTitle != null) {
            textViewTitle!!.setText(resId)
        }
    }

    private fun setInnerView(resId: Int) {
        val stub = findViewById<ViewStub>(R.id.viewStub)
        stub.layoutResource = resId
        innerView = stub.inflate() as LinearLayoutCompat
    }

    fun clearChildren() {
        innerView!!.removeAllViews()
    }

    fun addChildView(view: View?) {
        innerView!!.addView(view)
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        /*if (arrowIcon != null) {
            arrowIcon.setOnClickListener(l);
        }*/
        super.setOnClickListener(l)
    }

    private fun setAnimDuration(animDuration: Long) {
        this.animDuration = animDuration
    }
}