package de.bikebean.app.ui.utils.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import de.bikebean.app.ui.drawer.map.MapFragmentOld

object PermissionUtils {

    private val smsPermissions: Array<String>
        get() = when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.O -> smsPermissionsAndroid8_0
            else -> smsPermissionsAndroidX_X
        }

    private val smsPermissionsAndroid8_0 = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_PHONE_STATE
    )

    private val smsPermissionsAndroidX_X = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
    )

    private val permissionMap = mapOf(
            KEYS.SMS to smsPermissions,
            KEYS.MAPS to MapFragmentOld.mapsPermissions
    )

    private fun getPermissions(activity: AppCompatActivity, p: KEYS, tag: String): Boolean {
        (permissionMap[p] ?: return false).filter {
            checkSelfPermission(activity, it)
        }.let { unGrantedPermissions ->
            return unGrantedPermissions.isEmpty().also { isEmpty ->
                if (!isEmpty) handleDialog(activity, p, tag, unGrantedPermissions.first())
            }
        }
    }

    private fun getPermissions(fragment: Fragment, p: KEYS, tag: String): Boolean {
        (permissionMap[p] ?: return false).filter {
            checkSelfPermission(fragment.requireContext(), it)
        }.let { unGrantedPermissions ->
            return unGrantedPermissions.isEmpty().also { isEmpty ->
                if (!isEmpty) handleDialog(fragment, p, tag, unGrantedPermissions.first())
            }
        }
    }

    private fun handleDialog(activity: AppCompatActivity, p: KEYS, tag: String, perm: String) =
            when {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, perm) ->
                    showPermissionsRationaleDialog(activity, p, tag)
                else -> askForPermissions(activity, p)
            }

    private fun handleDialog(fragment: Fragment, p: KEYS, tag: String, perm: String) =
            when {
                fragment.shouldShowRequestPermissionRationale(perm) ->
                    showPermissionsRationaleDialog(fragment, p, tag)
                else -> askForPermissions(fragment, p)
            }

    private fun showPermissionsRationaleDialog(activity: AppCompatActivity, p: KEYS, tag: String) =
            PermissionsRationaleDialog(activity, p).show(activity.supportFragmentManager, tag)

    private fun showPermissionsRationaleDialog(fragment: Fragment, p: KEYS, tag: String) =
            PermissionsRationaleDialog(fragment, p).show(fragment.parentFragmentManager, tag)

    private fun checkSelfPermission(context: Context, permission: String): Boolean =
            (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED)

    @JvmStatic
    fun askForPermissions(activity: Activity, p: KEYS) {
        (permissionMap[p] ?: return).let { permissions ->
            ActivityCompat.requestPermissions(activity, permissions, p.ordinal)
        }
    }

    @JvmStatic
    fun askForPermissions(fragment: Fragment, p: KEYS) {
        (permissionMap[p] ?: return).let { permissions ->
            fragment.requestPermissions(permissions, p.ordinal)
        }
    }

    @JvmStatic
    fun checkResult(grantResults: IntArray): Boolean =
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

    @JvmStatic
    fun hasSmsPermissions(act: AppCompatActivity): Boolean =
            getPermissions(act, KEYS.SMS, "smsRationaleDialog")

    @JvmStatic
    fun hasSmsPermissions(fragment: Fragment): Boolean =
            getPermissions(fragment, KEYS.SMS, "smsRationaleDialog")

    @JvmStatic
    fun lacksSmsPermissions(context: Context): Boolean {
        return (permissionMap[KEYS.SMS] ?: return false).any {
            checkSelfPermission(context, it)
        }
    }

    @JvmStatic
    fun hasMapsPermissions(fragment: Fragment): Boolean =
            getPermissions(fragment, KEYS.MAPS, "mapsRationaleDialog")

    enum class KEYS {
        SMS, MAPS
    }
}