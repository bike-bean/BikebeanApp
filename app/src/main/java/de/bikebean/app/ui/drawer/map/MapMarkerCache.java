package de.bikebean.app.ui.drawer.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

import de.bikebean.app.R;

public class MapMarkerCache {

    /*
     Thanks to Nick Skelton:
     https://gist.github.com/shredderskelton/753c85d5cb790d686577605b46b06c4e#file-mybitmapcache-kt
     */

    private final @NonNull Context context;
    private final @NonNull LruCache<Integer, Bitmap> bitmapCache;

    private static final Map<DRAWABLES, FgBgMapping> FgBgMap = new HashMap<DRAWABLES, FgBgMapping>() {{
        put(DRAWABLES.mapMarker, new FgBgMapping(
                R.drawable.ic_bike_map_marker_fg,
                R.drawable.ic_bike_map_marker_bg)
        );
        put(DRAWABLES.lastChangedIndicator, new FgBgMapping(
                R.drawable.ic_bike_last_changed_indicator_fg,
                R.drawable.ic_bike_last_changed_indicator_bg)
        );
    }};

    public MapMarkerCache(final @NonNull Context context, int size) {
        this.context = context;
        bitmapCache = new LruCache<>(size);
    }

    public @Nullable Bitmap getBitmap(final @NonNull DRAWABLES drawable,
                                      final @ColorInt int tintColor) {
        /*
         each drawable/tint combination needs it's own record in the cache.
         For example, a red car marker (R.drawable.ic_car, R.color.red)
         and a blue car marker (R.drawable.ic_car, R.color.blue)
         and a green car marker (R.drawable.ic_car, R.color.green)
         are all different bitmaps, but all created with the same drawable
         */
        if (bitmapCache.get(hash(drawable.ordinal(), tintColor)) == null) {
            /* if it's not in the cache, create it */
            final @Nullable Bitmap bitmap = drawableToBitmap(context, drawable, tintColor);
            /* then add it to the cache */
            bitmapCache.put(hash(drawable.ordinal(), tintColor), bitmap);
        }

        return bitmapCache.get(hash(drawable.ordinal(), tintColor));
    }

    private int hash(final int a, final int b) {
        /*
         quick and dirty "hash" function
         */
        int hash = 17;
        hash = hash * 31 + a;
        hash = hash * 31 + b;

        return hash;
    }

    /* This is the "main" function that creates the Bitmap from a drawable file */
    private @Nullable Bitmap drawableToBitmap(final @NonNull Context context,
                                              final DRAWABLES drawable,
                                              final @ColorInt int color) {
        final @Nullable Drawable drawableBg = getDrawable(context, drawable, FgBgMapping.FgBg.BG);
        final @Nullable Drawable drawableFg = getDrawable(context, drawable, FgBgMapping.FgBg.FG);
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

    private @Nullable Drawable getDrawable(final @NonNull Context context,
                                           final DRAWABLES drawable,
                                           final FgBgMapping.FgBg fgBg) {
        final @Nullable FgBgMapping fgBgMapping = FgBgMap.get(drawable);
        if (fgBgMapping == null)
            return null;

        final @DrawableRes int drawableRes = fgBgMapping.get(fgBg);
        if (drawableRes == 0)
            return null;

        return ContextCompat.getDrawable(context, drawableRes);
    }

    public enum DRAWABLES {
        mapMarker, lastChangedIndicator
    }

    static class FgBgMapping {
        private final @DrawableRes int fg;
        private final @DrawableRes int bg;

        public enum FgBg {
            FG, BG
        }

        public FgBgMapping(int fg, int bg) {
            this.fg = fg;
            this.bg = bg;
        }

        public @DrawableRes int get(final FgBg fgBg) {
            if (fgBg == FgBg.FG) return fg;
            else if (fgBg == FgBg.BG) return bg;
            else return 0;
        }
    }
}
