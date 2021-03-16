package de.bikebean.app.ui.drawer.status.settings

import android.view.View
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import de.bikebean.app.MainActivity
import de.bikebean.app.ui.drawer.status.ProgressView
import de.bikebean.app.ui.drawer.status.settings.LiveDataTimerViewModel.TIMER
import de.bikebean.app.ui.utils.resource.ResourceUtils.getCurrentIconColorFilter
import de.bikebean.app.ui.utils.resource.ResourceUtils.getCurrentTextColorFilter

abstract class SettingsStatusSubView(
        private val cardView: MaterialCardView,
        internal val icon: ImageView,
        internal val subTitle: TextView,
        internal val progressView: ProgressView,
        internal val t: TIMER
) {

    abstract val helpResId: Int
    abstract val titleResId: Int
    abstract val onCardViewClickListener: (View) -> Unit

    abstract fun setupListeners(l: LifecycleOwner, f: SettingsStatusFragment)

    abstract fun resetElements(f: SettingsStatusFragment)

    open fun initUserInteractionElements(f: SettingsStatusFragment) {
        icon.colorFilter = getCurrentIconColorFilter((f.requireActivity() as MainActivity))

        cardView.setOnClickListener(onCardViewClickListener)

        getSettingsTitleTextView(cardView).setText(titleResId)
        getSettingsHelpButton(cardView).apply {
            setOnClickListener(::onHelpClick)
            colorFilter = getCurrentTextColorFilter(f.requireContext())
        }
    }

    private fun onHelpClick(v: View) {
        Snackbar.make(v, helpResId, Snackbar.LENGTH_LONG).show()
    }

    private fun getSettingsTitleTextView(v: MaterialCardView): TextView {
        return getSettingsTitleLinearLayout(v).getChildAt(0) as TextView
    }

    private fun getSettingsHelpButton(v: MaterialCardView): ImageView {
        return getSettingsTitleLinearLayout(v).getChildAt(2) as ImageView
    }

    private fun getSettingsTitleLinearLayout(v: MaterialCardView): LinearLayout {
        val l = v.getChildAt(0) as LinearLayout
        return l.getChildAt(0) as LinearLayout
    }
}