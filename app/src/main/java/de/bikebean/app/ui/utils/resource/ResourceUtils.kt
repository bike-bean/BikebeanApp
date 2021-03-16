package de.bikebean.app.ui.utils.resource

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import de.bikebean.app.MainActivity
import de.bikebean.app.R
import de.bikebean.app.db.settings.settings.add_to_list_settings.Battery.Companion.UNSET_BATTERY

object ResourceUtils {

    @JvmStatic
    @ColorInt
    fun getMarkerColor(context: Context, daysSinceState: Double): Int {
        if (daysSinceState == Double.MAX_VALUE)
            return getAttributeErrorColor(context)

        val d = floorToOne(daysSinceState.toFloat() / 365)
        // float d = 1f;  /* for tests */
        return HslColor(context, R.color.brandColorGreen).getCodedMarkerColor(d)
    }

    @ColorInt
    private fun getAttributeErrorColor(context: Context): Int =
            getAttributeColor(context, R.attr.colorError)

    @ColorInt
    fun getAttributeColor(context: Context, @AttrRes attr: Int): Int = TypedValue().let {
        context.theme.resolveAttribute(attr, it, true)
        it.data
    }

    @JvmStatic
    fun getCurrentIconColorFilter(activity: MainActivity): ColorFilter = when {
        activity.isLightTheme -> getAttributeColor(activity, R.attr.colorPrimary)
        else -> ContextCompat.getColor(activity, R.color.white)
    }.let {
        PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
    }

    @ColorInt
    fun getCurrentTextColor(context: Context): Int =
            getAttributeColor(context, R.attr.colorOnSurface)

    @JvmStatic
    fun getCurrentTextColorFilter(context: Context): ColorFilter =
            PorterDuffColorFilter(getCurrentTextColor(context), PorterDuff.Mode.SRC_IN)

    /*
    fun getActionBarSize(context: Context): Int = TypedValue().let {
        context.theme.resolveAttribute(R.attr.actionBarSize, it, true)
        TypedValue.complexToDimensionPixelSize(
                it.data,
                context.resources.displayMetrics
        )
    }
     */

    private fun floorToOne(a: Float): Float = if (a > 1) 1f else a

    private val batteryDrawables = mapOf(
            100.0 to R.drawable.ic_battery_full_black_24dp,
            90.0 to R.drawable.ic_battery_90_black_24dp,
            80.0 to R.drawable.ic_battery_80_black_24dp,
            60.0 to R.drawable.ic_battery_60_black_24dp,
            50.0 to R.drawable.ic_battery_50_black_24dp,
            30.0 to R.drawable.ic_battery_30_black_24dp,
            20.0 to R.drawable.ic_battery_20_black_24dp,
            0.0 to R.drawable.ic_battery_alert_red_24dp,
            UNSET_BATTERY to R.drawable.ic_battery_unknown_black_24dp
    )

    @JvmStatic
    fun getBatteryDrawable(ctx: Context, batteryStatus: Double): Drawable? {
        val flooredBatteryStatus = floorToBatterySteps(batteryStatus)

        @DrawableRes
        val batteryDrawableId: Int? = if (batteryDrawables.containsKey(flooredBatteryStatus))
            batteryDrawables[flooredBatteryStatus]
        else return null

        batteryDrawableId ?: return null

        val drawable = ContextCompat.getDrawable(ctx, batteryDrawableId) ?: return null
        val batteryColor = HslColor(141f, 0.5f, 0.5f)

        DrawableCompat.setTint(
                drawable,
                batteryColor.getCodedBatteryColor((batteryStatus / 100).toFloat())
        )

        return ContextCompat.getDrawable(ctx, batteryDrawableId)
    }

    private fun floorToBatterySteps(batteryStatus: Double): Double =
            batteryDrawables.keys.let {
                it.sortedDescending().firstOrNull { batteryStatusFloored ->
                    batteryStatus >= batteryStatusFloored
                } ?: -1.0
            }

    fun getIntervalString(position: Int, context: Context): String =
            context.resources.getStringArray(R.array.interval_values)[position]

    internal class HslColor {

        private val h : Float
        private val s : Float
        private val l : Float

        @ColorInt
        fun getCodedMarkerColor(d: Float): Int =
                ColorUtils.HSLToColor(floatArrayOf(h, s * (1 - d), (l + 0.33 * d).toFloat()))

        @ColorInt
        fun getCodedBatteryColor(d: Float): Int = when {
            d < 0 -> ColorUtils.HSLToColor(floatArrayOf(0f, 0f, 0f))
            else -> ColorUtils.HSLToColor(floatArrayOf(d * h, s, l))
        }

        constructor(_h: Float, _s: Float, _l: Float) {
            h = _h; s = _s; l = _l
        }

        constructor(context: Context, @ColorRes color: Int) {
            FloatArray(3).let {
                ColorUtils.colorToHSL(ContextCompat.getColor(context, color), it)
                h = it[0]; s = it[1]; l = it[2]
            }
        }
    }
}