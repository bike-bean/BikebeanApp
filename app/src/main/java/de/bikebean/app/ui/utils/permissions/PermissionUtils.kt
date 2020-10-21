package de.bikebean.app.ui.utils.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.bikebean.app.ui.drawer.map.MapFragment

object PermissionUtils {

    val smsPermissions: Array<String>
        get() = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            smsPermissionsAndroid8_0
        } else {
            smsPermissionsAndroidX_X
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

    private val permissionMap: HashMap<KEYS, Array<String>> = object : HashMap<KEYS, Array<String>>() {
        init {
            put(KEYS.SMS, smsPermissions)
            put(KEYS.MAPS, MapFragment.mapsPermissions)
        }
    }

    @JvmStatic
    fun getPermissions(activity: AppCompatActivity, p: KEYS, tag: String): Boolean {
        val permissions = permissionMap[p] ?: return false

        for (perm in permissions)
            if (checkSelfPermission(activity, perm))
                return if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                    PermissionsRationaleDialog(activity, p).show(
                            activity.supportFragmentManager, tag
                    )
                    false
                } else {
                    askForPermissions(activity, p)
                    false
                }

        return true
    }

    private fun checkSelfPermission(context: Context, permission: String): Boolean {
        return (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED)
    }

    @JvmStatic
    fun askForPermissions(activity: Activity, p: KEYS) {
        val permissions = permissionMap[p] ?: return

        ActivityCompat.requestPermissions(activity, permissions, p.ordinal)
    }

    @JvmStatic
    fun checkResult(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    fun hasSmsPermissions(act: AppCompatActivity): Boolean {
        return getPermissions(act, KEYS.SMS, "smsRationaleDialog")
    }

    @JvmStatic
    fun hasMapsPermissions(act: AppCompatActivity): Boolean {
        return getPermissions(act, KEYS.MAPS, "mapsRationaleDialog")
    }

    enum class KEYS {
        SMS, MAPS
    }
}