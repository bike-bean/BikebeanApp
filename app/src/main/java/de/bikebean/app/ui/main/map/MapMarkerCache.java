package de.bikebean.app.ui.main.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import de.bikebean.app.R;

public class MapMarkerCache {

    /*
     Thanks to Nick Skelton:
     https://gist.github.com/shredderskelton/753c85d5cb790d686577605b46b06c4e#file-mybitmapcache-kt
     */

    private final @NonNull Context context;
    private final @NonNull LruCache<Integer, Bitmap> bitmapCache;

    private static final int mapMarkerDrawableBg = R.drawable.ic_bike_map_marker_bg;
    private static final int mapMarkerDrawableFg = R.drawable.ic_bike_map_marker_fg;

    public MapMarkerCache(final @NonNull Context context, int size) {
        this.context = context;
        bitmapCache = new LruCache<>(size);
    }

    public @Nullable Bitmap getBitmap(@ColorInt int tintColor) {
        /*
         each drawable/tint combination needs it's own record in the cache.
         For example, a red car marker (R.drawable.ic_car, R.color.red)
         and a blue car marker (R.drawable.ic_car, R.color.blue)
         and a green car marker (R.drawable.ic_car, R.color.green)
         are all different bitmaps, but all created with the same drawable
         */
        if (bitmapCache.get(tintColor) == null) {
            /* if it's not in the cache, create it */
            final @Nullable Bitmap bitmap = drawableToBitmap(context, tintColor);
            /* then add it to the cache */
            bitmapCache.put(tintColor, bitmap);
        }

        return bitmapCache.get(tintColor);
    }

    /* This is the "main" function that creates the Bitmap from a drawable file */
    private @Nullable Bitmap drawableToBitmap(final @NonNull Context context, @ColorInt int color) {
        final @Nullable Drawable drawableBg =
                ContextCompat.getDrawable(context, mapMarkerDrawableBg);
        final @Nullable Drawable drawableFg =
                ContextCompat.getDrawable(context, mapMarkerDrawableFg);
        if (drawableBg == null || drawableFg == null)
            return null;

        drawableBg.setBounds(
                0, 0,
                drawableFg.getIntrinsicWidth(),
                drawableFg.getIntrinsicHeight()
        );
        drawableFg.setBounds(
                0, 0,
                drawableFg.getIntrinsicWidth(),
                drawableFg.getIntrinsicHeight()
        );

        final @NonNull Bitmap bitmapBg = Bitmap.createBitmap(
                drawableBg.getIntrinsicWidth(),
                drawableBg.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        drawableFg.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));

        final @NonNull Canvas canvas = new Canvas(bitmapBg);
        drawableBg.draw(canvas);
        drawableFg.draw(canvas);

        return bitmapBg;
    }
}
