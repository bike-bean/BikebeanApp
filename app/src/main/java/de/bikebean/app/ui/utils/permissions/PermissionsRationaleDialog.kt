package de.bikebean.app.ui.utils.permissions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import de.bikebean.app.R
import de.bikebean.app.ui.utils.permissions.PermissionUtils.KEYS
import de.bikebean.app.ui.utils.permissions.PermissionUtils.askForPermissions

class PermissionsRationaleDialog(
        private val permissionKey: KEYS,
        private val _context: Context,
        private val act: Activity? = null,
        private val fragment: Fragment? = null,
) : DialogFragment() {

    constructor(act: Activity, permissionKey: KEYS) : this(
            permissionKey, act, act
    )

    constructor(fragment: Fragment, permissionKey: KEYS) : this(
            permissionKey, fragment.requireContext(), fragment = fragment
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val (title, message) = permissionMap[permissionKey] ?: default

        val buttonClickListener: ((DialogInterface, Int) -> Unit)? = when {
            act != null -> { _, _ -> askForPermissions(act, permissionKey) }
            fragment != null ->  { _, _ -> askForPermissions(fragment, permissionKey) }
            else -> null
        }

        return AlertDialog.Builder(_context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.continue_ok, buttonClickListener)
        }.create()
    }

    companion object {
        private val permissionMap = mapOf(
                KEYS.SMS to (R.string.no_sms_permission to R.string.no_sms_permission_warning),
                KEYS.MAPS to (R.string.no_map_permission to R.string.no_map_permission_warning)
        )

        private val default = R.string.generic_permission to R.string.generic_permission_warning
    }
}