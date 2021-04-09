package de.bikebean.app.ui.initialization.views

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.status.StateViewModel
import de.bikebean.app.ui.utils.Utils
import de.bikebean.app.ui.utils.preferences.PreferencesUtils

class PhoneNumberInitialView(
        context: Context,
        attrs: AttributeSet?,
        parentList: InitialItemViewList?)
    : InitialItemView(context, attrs, POSITION, parentList) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null)

    private var preferences: SharedPreferences? = null

    private var editText: EditText? = null

    override val itemAdditionalLayout: TextInputLayout = run {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.text_input, itemLargeLayout, false)
                .findViewById(R.id.textInput)
    }

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
        addView(itemAdditionalLayout)
        addView(itemButtonLayout)
    }

    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        itemActionButtonPrimary.setOnClickListener{ onButtonClicked() }
        editText = itemAdditionalLayout.editText

        editText?.setOnKeyListener{ _, _, _ -> listenKeys() }

        updateItemsDoneState()
        updateVisibility()

        addView(itemLargeLayout)
    }

    private fun onButtonClicked() {
        editText ?: return

        editText?.text.toString().run {
            Utils.getErrorString(this)?.let {
                itemAdditionalLayout.error = context.getString(it)
                when (it) {
                    R.string.message_pref_number_no_blanks -> editText?.setText(Utils.eliminateSpaces(this))
                }
                hideSoftKeyboard(context, editText)
                return
            }

            hideSoftKeyboard(context, editText)
            PreferencesUtils.setInitStateAddress(preferences, this)?.let {
                onClickListenerCallback?.invoke()
            }
        }
    }

    private fun hideSoftKeyboard(context: Context?, view: View?) =
            (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(view?.windowToken, 0)

    private fun listenKeys(): Boolean = when {
        !Utils.beginsWithPlus(editText?.text.toString()) -> {
            itemAdditionalLayout.error = context.getString(R.string.message_pref_number)
            false
        }
        else -> {
            itemAdditionalLayout.error = null
            false
        }
    }

    override fun checkIfActive(st: StateViewModel): Boolean =
            PreferencesUtils.getBikeBeanNumber(context) == null

    override fun updateItemsDoneState() = when (doneState) {
        CHECKED -> {
            itemSubTitle.text = itemSubtitlesChecked[POSITION].format(
                    PreferencesUtils.getBikeBeanNumber(context)
            )
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
        const val POSITION = 0
    }
}