package de.bikebean.app.ui.initialization.views

import android.content.Context
import android.graphics.Color
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.status.StateViewModel
import de.bikebean.app.ui.utils.resource.ResourceUtils.getCurrentTextColor

abstract class InitialItemView(
        context: Context,
        attrs: AttributeSet?,
        val position: Int,
        private val parentList: InitialItemViewList?)
    : MaterialCardView(context, attrs) {

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0, null)

    protected val margin =
            context.resources.getDimensionPixelOffset(R.dimen.fragment_vertical_margin)
    private val circleCheckedDrawable =
            ContextCompat.getDrawable(context, R.drawable.circle_checked)
    private val circlePlainDrawable =
            ContextCompat.getDrawable(context, R.drawable.circle_plain)
    private val circleGrayDrawable =
            ContextCompat.getDrawable(context, R.drawable.circle_gray)
    private val initialItemTransition = TransitionInflater
            .from(context)
            .inflateTransition(R.transition.ititial_item_transition)
    private val whiteColor = Color.WHITE
    private val greyColor = ContextCompat.getColor(context, R.color.middleGrey)

    private val itemPrimaryButtons: Array<String> =
            context.resources.getStringArray(R.array.initial_item_primary_buttons)
    protected val itemSecondaryButtons: Array<String> =
            context.resources.getStringArray(R.array.initial_item_secondary_buttons)
    protected val itemSubtitles: Array<String> =
            context.resources.getStringArray(R.array.initial_item_subtitles)
    protected val itemSubtitlesChecked: Array<String> =
            context.resources.getStringArray(R.array.initial_item_subtitles_checked)
    private val itemDescriptions: Array<String> =
            context.resources.getStringArray(R.array.initial_item_descriptions)

    private val itemNumber: TextView = TextView(context).apply {
        layoutParams = getTextViewLayoutParams()
        gravity = Gravity.CENTER
        setOnClickListener { onClickHandler() }
    }

    private val itemDescription: TextView = TextView(context).apply {
        layoutParams = getTextViewLayoutParams(margin)
        gravity = Gravity.CENTER_VERTICAL
        setOnClickListener { onClickHandler() }
    }

    protected val itemActionButtonPrimary: Button = MaterialButton(
            context, null, R.attr.materialButtonStyle
    ).apply {
        layoutParams = getTextViewLayoutParams(margin)
        text = itemPrimaryButtons[position]
    }

    protected val itemSubTitle: TextView = TextView(context).apply {
        layoutParams = getTextViewLayoutParams()
        gravity = Gravity.CENTER_VERTICAL
        text = itemSubtitles[position]
        setOnClickListener { onClickHandler() }

        setPadding(margin, margin, margin, margin)
    }

    protected val itemLayout: LinearLayout = LinearLayout(context).apply {
        layoutParams = getLinearLayoutParams()
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setOnClickListener { onClickHandler() }

        setPadding(margin, margin, margin, margin)

        addView(itemNumber)
        addView(itemDescription)
    }

    protected abstract val itemActionButtonSecondary: Button

    protected abstract val itemButtonLayout: LinearLayout

    abstract val itemAdditionalLayout: View?

    protected abstract val itemLargeLayout: LinearLayout

    init {
        initItemSubtitle(itemSubTitle)
        initItemNumber(itemNumber)
        initItemDescription(itemDescription)

        layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = margin
            leftMargin = margin
            rightMargin = margin
        }
    }

    // only to make kotlin lint happy (regarding calling non-final functions from constructor)
    final override fun addView(child: View) {
        super.addView(child)
    }

    protected fun setAdditionalItemVisibility(visibility: Int) = visibility.let {
        itemAdditionalLayout?.visibility = it
    }

    private fun initItemSubtitle(view: TextView) =
            setTextAppearance(view, R.attr.textAppearanceBody2)

    private fun initItemNumber(view: TextView) =
            setTextAppearance(view, R.attr.textAppearanceHeadline6)

    private fun initItemDescription(view: TextView) =
            setTextAppearance(view, R.attr.textAppearanceBody1)

    var onClickListenerCallback: (() -> Unit)? = null
    var onClickListenerSkipCallback: ((initialItemView: InitialItemView) -> Unit)? = null

    abstract fun checkIfActive(st: StateViewModel): Boolean

    var userSkip: Boolean = false

    var clickState: Int = COLLAPSED
        set(value) {
            field = value
            updateVisibility()
        }

    var doneState: Int = ACTIVE
        set(value) {
            field = value
            updateVisibility()
            updateItemsDoneState()
        }

    open fun updateItemsDoneState() : Any? = when (doneState) {
        CHECKED -> {
            setTextEmpty()
            itemNumber.background = circleCheckedDrawable
            itemDescription.setTextColor(getCurrentTextColor(context))
        }
        ACTIVE -> {
            setTextPosition()
            itemNumber.apply {
                background = circlePlainDrawable
                setTextColor(whiteColor)
            }
            itemDescription.setTextColor(getCurrentTextColor(context))
        }
        FUTURE -> {
            setTextPosition()
            itemNumber.apply {
                background = circleGrayDrawable
                setTextColor(greyColor)
            }
            itemDescription.setTextColor(greyColor)
        }
        else -> null
    }

    open fun updateVisibility() = when (clickState) {
        COLLAPSED -> View.GONE to View.GONE
        EXPANDED -> when (doneState) {
            ACTIVE -> View.VISIBLE to View.VISIBLE
            CHECKED -> View.GONE to View.VISIBLE
            FUTURE -> View.GONE to View.GONE
            else -> null
        }
        else -> null
    }?.let {
        itemButtonLayout.visibility = it.first
        itemSubTitle.visibility = it.second
    }

    override fun setClickable(clickable: Boolean) = clickable.let {
        itemNumber.isClickable = it
        itemDescription.isClickable = it
        itemLayout.isClickable = it
        itemSubTitle.isClickable = it
        itemLargeLayout.isClickable = it
    }.also { super.setClickable(clickable) }

    private fun setTextPosition() = try {
        setText(itemDescriptions[position], "${position + 1}")
    } catch (e: IndexOutOfBoundsException) {
        setText()
    }

    private fun setTextEmpty() = try {
        setText(itemDescriptions[position])
    } catch (e: IndexOutOfBoundsException) {
        setText()
    }

    private fun setText(textDescription: String = "", textNumber: String = "") {
        itemDescription.text = textDescription
        itemNumber.text = textNumber
    }

    protected fun onClickHandler() = when (clickState) {
        EXPANDED -> resizeWithTransition(COLLAPSED)
        COLLAPSED -> when (parentList) {
            null -> resizeWithTransition(EXPANDED)
            else -> parentList.collapseAllExceptOne(this)
        }
        else -> null
    }

    fun resizeWithTransition(state: Int) {
        TransitionManager.beginDelayedTransition(this, initialItemTransition)
        clickState = state
    }

    private fun setTextAppearance(text: TextView, @AttrRes themeAttr: Int) =
            TextViewCompat.setTextAppearance(text, getStyleResource(context, themeAttr))

    companion object {

        const val COLLAPSED : Int = 0
        const val EXPANDED : Int = 1

        const val NOT_CONFIGURED: Int = 0
        const val CHECKED: Int = 1
        const val ACTIVE: Int = 2
        const val FUTURE: Int = 3

        @StyleRes
        fun getStyleResource(context: Context, @AttrRes themeAttr: Int): Int =
                TypedValue().let { typedValue ->
                    context.theme.resolveAttribute(themeAttr, typedValue, true)
                    typedValue.resourceId  // return
                }

        fun getLinearLayoutParams(): LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        )

        fun getTextViewLayoutParams(_marginStart: Int = 0): LayoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = _marginStart
        }
    }
}