package de.bikebean.app.ui.utils.resource

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import de.bikebean.app.R
import de.bikebean.app.db.settings.settings.Battery.UNSET_BATTERY
import java.util.*
import kotlin.collections.HashMap

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
    private fun getAttributeErrorColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorError, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @JvmStatic
    fun getActionBarSize(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.actionBarSize, typedValue, true)
        return TypedValue.complexToDimensionPixelSize(
                typedValue.data,
                context.resources.displayMetrics
        )
    }

    private fun floorToOne(a: Float): Float {
        return if (a > 1) 1f else a
    }

    private val batteryDrawables: Map<Double, Int> = object : HashMap<Double, Int>() {
        init {
            put(100.0, R.drawable.ic_battery_full_black_24dp)
            put(90.0, R.drawable.ic_battery_90_black_24dp)
            put(80.0, R.drawable.ic_battery_80_black_24dp)
            put(60.0, R.drawable.ic_battery_60_black_24dp)
            put(50.0, R.drawable.ic_battery_50_black_24dp)
            put(30.0, R.drawable.ic_battery_30_black_24dp)
            put(20.0, R.drawable.ic_battery_20_black_24dp)
            put(0.0, R.drawable.ic_battery_alert_red_24dp)
            put(UNSET_BATTERY, R.drawable.ic_battery_unknown_black_24dp)
        }
    }

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

    private fun floorToBatterySteps(batteryStatus: Double): Double {
        val doubles: Array<Double> = batteryDrawables.keys.toTypedArray()
        Arrays.sort(doubles, Collections.reverseOrder())

        for (d in doubles) if (batteryStatus >= d) return d
        return -1.0
    }

    internal class HslColor {

        private val h : Float
        private val s : Float
        private val l : Float

        @ColorInt
        fun getCodedMarkerColor(d: Float): Int {
            return ColorUtils.HSLToColor(floatArrayOf(h, s * (1 - d), (l + 0.33 * d).toFloat()))
        }

        @ColorInt
        fun getCodedBatteryColor(d: Float): Int {
            return if (d >= 0)
                ColorUtils.HSLToColor(floatArrayOf(d * h, s, l))
            else
                ColorUtils.HSLToColor(floatArrayOf(0f, 0f, 0f))
        }

        constructor(_h: Float, _s: Float, _l: Float) {
            h = _h; s = _s; l = _l
        }

        constructor(context: Context, @ColorRes color: Int) {
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(ContextCompat.getColor(context, color), hsl)

            h = hsl[0]; s = hsl[1]; l = hsl[2]
        }
    }
}