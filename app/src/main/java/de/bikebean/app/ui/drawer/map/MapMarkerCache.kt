package de.bikebean.app.ui.drawer.map

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.map.MapMarkerCache.FgBgMapping.FgBg

/*
 Thanks to Nick Skelton:
 https://gist.github.com/shredderskelton/753c85d5cb790d686577605b46b06c4e#file-mybitmapcache-kt
 */
class MapMarkerCache(
        private val context: Context,
        size: Int
) {

    private val bitmapCache: LruCache<Int, Bitmap?> = LruCache(size)

    companion object {
        private val FgBgMap = mapOf(
                DRAWABLES.MapMarker to FgBgMapping(
                        R.drawable.ic_bike_map_marker_fg,
                        R.drawable.ic_bike_map_marker_bg
                ),
                DRAWABLES.LastChangedIndicator to FgBgMapping(
                        R.drawable.ic_bike_last_changed_indicator_fg,
                        R.drawable.ic_bike_last_changed_indicator_bg
                )
        )
    }

    /*
     Each drawable/tint combination needs it's own record in the cache.
     For example, a red car marker (R.drawable.ic_car, R.color.red)
     and a blue car marker (R.drawable.ic_car, R.color.blue)
     and a green car marker (R.drawable.ic_car, R.color.green)
     are all different bitmaps, but all created with the same drawable */
    fun getBitmap(drawable: DRAWABLES, @ColorInt tintColor: Int): Bitmap? =
            bitmapCache[hash(drawable.ordinal, tintColor)].let { cachedBitmap ->
                cachedBitmap ?: run {
                    /* if it's not in the cache, create it */
                    drawableToBitmap(context, drawable, tintColor).also { newBitmap ->
                        /* also add it to the cache */
                        bitmapCache.put(hash(drawable.ordinal, tintColor), newBitmap)
                    }
                }
            }

    fun getBitmap(drawable: Drawable, @ColorInt tintColor: Int): Bitmap =
            bitmapCache[hash(DRAWABLES.Other.ordinal, tintColor)].let { cachedBitmap ->
                cachedBitmap ?: run {
                    /* if it's not in the cache, create it */
                    drawableToBitmapSimple(drawable, tintColor).also { newBitmap ->
                        /* also add it to the cache */
                        bitmapCache.put(hash(DRAWABLES.Other.ordinal, tintColor), newBitmap)
                    }
                }
            }

    private fun hash(a: Int, b: Int): Int {
        /*
         quick and dirty "hash" function
         */
        var hash = 17
        hash = hash * 31 + a
        hash = hash * 31 + b
        return hash
    }

    /* This is the "main" function that creates the Bitmap from a drawable file */
    private fun drawableToBitmap(context: Context,
                                 drawable: DRAWABLES,
                                 @ColorInt color: Int): Bitmap? {
        val drawableBg = (getDrawable(context, drawable, FgBg.BG) ?: return null).apply {
            setBounds(
                    0, 0,
                    intrinsicWidth,
                    intrinsicHeight
            )
        }

        val drawableFg = (getDrawable(context, drawable, FgBg.FG) ?: return null).apply {
            setBounds(
                    0, 0,
                    intrinsicWidth,
                    intrinsicHeight
            )
            colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        val bitmapBg = Bitmap.createBitmap(
                drawableBg.intrinsicWidth,
                drawableBg.intrinsicHeight,
                Bitmap.Config.ARGB_8888
        )

        Canvas(bitmapBg).let {
            drawableBg.draw(it)
            drawableFg.draw(it)
        }
        return bitmapBg
    }

    private fun drawableToBitmapSimple(drawable: Drawable,
                                       @ColorInt color: Int): Bitmap {
        drawable.apply {
            colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            setBounds(
                    0, 0,
                    intrinsicWidth,
                    intrinsicHeight
            )
        }

        val bitmap : Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
        )

        drawable.draw(Canvas(bitmap))

        return bitmap
    }

    private fun getDrawable(context: Context,
                            drawable: DRAWABLES,
                            fgBg: FgBg): Drawable? {
        @DrawableRes val drawableRes = (FgBgMap[drawable] ?: return null)[fgBg]
        return if (drawableRes == 0) null else ContextCompat.getDrawable(context, drawableRes)
    }

    enum class DRAWABLES {
        MapMarker, LastChangedIndicator, Other
    }

    internal class FgBgMapping(
            @field:DrawableRes private val fg: Int,
            @field:DrawableRes private val bg: Int) {

        enum class FgBg {
            FG, BG
        }

        @DrawableRes
        operator fun get(fgBg: FgBg): Int = when (fgBg) {
            FgBg.FG -> fg
            FgBg.BG -> bg
        }
    }

}