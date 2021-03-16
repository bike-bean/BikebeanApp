package de.bikebean.app.ui.initialization.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.status.StateViewModel

class FinishInitialView(
        context: Context,
        attrs: AttributeSet?,
        parentList: InitialItemViewList?)
    : InitialItemView(context, attrs, POSITION, parentList) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null)

    override val itemAdditionalLayout: View? = null

    override val itemActionButtonSecondary: Button = MaterialButton(
            context, null, R.attr.materialButtonOutlinedStyle
    ).apply {
        layoutParams = getTextViewLayoutParams()
        visibility = View.GONE
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
        addView(itemButtonLayout)
    }

    init {
        itemActionButtonPrimary.setOnClickListener { onClickListenerCallback?.invoke() }

        updateItemsDoneState()
        updateVisibility()

        addView(itemLargeLayout)
    }

    override fun checkIfActive(st: StateViewModel): Boolean = true

    override fun updateItemsDoneState() = when (doneState) {
        CHECKED -> {
            itemSubTitle.text = itemSubtitlesChecked[POSITION]
            isClickable = false
        }
        ACTIVE -> {
            itemSubTitle.text = itemSubtitles[POSITION]
            isClickable = false
        }
        FUTURE -> isClickable = false
        else -> null
    }.also { super.updateItemsDoneState() }

    companion object {
        const val POSITION = 5
    }
}