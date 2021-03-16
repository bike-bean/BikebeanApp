package de.bikebean.app.ui.initialization.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.android.material.button.MaterialButton
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.status.StateViewModel

class IntervalInitialView(
        context: Context,
        attrs: AttributeSet?,
        parentList: InitialItemViewList?)
    : InitialItemView(context, attrs, POSITION, parentList) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null)

    private val _adapter = ArrayAdapter.createFromResource(
            context,
            R.array.interval_entries,
            android.R.layout.simple_spinner_item
    ).apply {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override val itemAdditionalLayout: Spinner = Spinner(context, Spinner.MODE_DIALOG).apply {
        layoutParams = getTextViewLayoutParams()
        gravity = Gravity.END
        adapter = _adapter
    }

    override val itemActionButtonSecondary: Button = MaterialButton(
            context, null, R.attr.materialButtonOutlinedStyle
    ).apply {
        layoutParams = getTextViewLayoutParams()
        text = itemSecondaryButtons[POSITION]
        setOnClickListener{
            onClickListenerSkipCallback?.invoke(this@IntervalInitialView)
        }
    }

    override val itemButtonLayout: LinearLayout = LinearLayout(context).apply {
        layoutParams = getLinearLayoutParams()
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.END

        setPadding(margin, margin, margin, margin)
        addView(itemActionButtonSecondary)
        addView(itemActionButtonPrimary)
    }

    override val itemLargeLayout: LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setOnClickListener { onClickHandler() }

        addView(itemLayout)
        addView(itemSubTitle)
        addView(itemAdditionalLayout)
        addView(itemButtonLayout)
    }

    init {
        itemActionButtonPrimary.setOnClickListener{ onClickListenerCallback?.invoke() }

        updateItemsDoneState()
        updateVisibility()

        addView(itemLargeLayout)
    }

    override fun checkIfActive(st: StateViewModel): Boolean = !st.isIntervalConfirmedSync

    override fun updateItemsDoneState() = when (doneState) {
        CHECKED -> {
            itemSubTitle.text = itemSubtitlesChecked[POSITION]
            isClickable = true
        }
        ACTIVE -> {
            itemSubTitle.text = itemSubtitles[POSITION]
            isClickable = true
        }
        FUTURE -> isClickable = false
        else -> null
    }.also { super.updateItemsDoneState() }

    override fun updateVisibility() = when (clickState) {
        COLLAPSED -> View.GONE
        EXPANDED -> when (doneState) {
            ACTIVE -> View.VISIBLE
            CHECKED -> View.GONE
            FUTURE -> View.GONE
            else -> null
        }
        else -> null
    }?.let(::setAdditionalItemVisibility).also {
        super.updateVisibility()
    }

    companion object {
        const val POSITION = 3
    }
}