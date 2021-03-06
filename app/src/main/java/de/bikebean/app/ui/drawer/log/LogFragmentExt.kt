package de.bikebean.app.ui.drawer.log

import com.google.android.material.snackbar.Snackbar
import de.bikebean.app.db.createReport

fun LogFragment.onSendButtonClick() {
    LogUploadWarnDialog(requireActivity(), ::generateLogAndUpload).show(
            requireActivity().supportFragmentManager, "logUploadDialog"
    )
}

fun LogFragment.generateLogAndUpload(extraText: String) {
    logViewModel.d("Exporting database...")

    showSnackbarText("Fehlerbericht senden...")

    createReport(requireContext(), logViewModel, ::notifyUploadSuccess, extraText).execute()
}

fun LogFragment.notifyUploadSuccess(success: Boolean) = when {
    success -> "Fehlerbericht gesendet"
    else -> "Fehlerbericht konnte nicht gesendet werden!"
}.let(::showSnackbarText)

private fun LogFragment.showSnackbarText(string: String) =
        Snackbar.make(
                sendButton,
                string,
                Snackbar.LENGTH_LONG
        ).show()