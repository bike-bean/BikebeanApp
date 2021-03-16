package de.bikebean.app.ui.utils

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import de.bikebean.app.R

object Utils {

    /* Number Preference Error Parsing */
    @JvmStatic
    @StringRes
    fun getErrorString(number: String): Int? = when {
        number.isEmpty() -> R.string.number_error
        !beginsWithPlus(number) -> R.string.number_subtitle
        hasSpaces(number) -> R.string.number_no_blanks
        else -> null
    }

    fun beginsWithPlus(s: String): Boolean = when {
        s.isEmpty() -> true
        else -> s.substring(0, 1) == "+"
    }

    private fun hasSpaces(s: String): Boolean = s.contains(" ")

    @JvmStatic
    fun eliminateSpaces(s: String): String = s.replace(" ", "")

    /* Share Button and Help Button */
    @JvmStatic
    fun getShareIntent(string: String): Intent? = when {
        string.isEmpty() -> null
        else -> Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, string)
                .setType("text/plain")
                .let {
                    Intent.createChooser(it, null)
                }
    }

    @JvmStatic
    fun getRouteIntent(uri: Uri): Intent = Intent(Intent.ACTION_VIEW, uri)
            .setPackage("com.google.android.apps.maps")

    @JvmStatic
    fun onHelpClick(v: View) {
        Snackbar.make(v,
                R.string.helpBattery,
                Snackbar.LENGTH_LONG)
                //                .setAction(R.string.history, (v1 -> {}))
                .show()
    }
}