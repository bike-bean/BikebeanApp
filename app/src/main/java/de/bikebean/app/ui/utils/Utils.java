package de.bikebean.app.ui.utils;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.R;

public class Utils {

    /* Number Preference Error Parsing */

    public static @Nullable @StringRes Integer getErrorString(final @NonNull String number) {
        if (number.isEmpty())
            return R.string.number_error;
        if (!beginsWithPlus(number))
            return R.string.number_subtitle;
        if (hasSpaces(number))
            return R.string.number_no_blanks;
        else return null;
    }

    public static boolean beginsWithPlus(final @NonNull String s) {
        if (s.length() < 1)
            return true;

        return s.substring(0, 1).equals("+");
    }

    private static boolean hasSpaces(final @NonNull String s) {
        return s.contains(" ");
    }

    public static @NonNull String eliminateSpaces(final @NonNull String s) {
        return s.replace(" ", "");
    }

    /* */

    /* Share Button and Help Button */

    public static @Nullable Intent getShareIntent(final @NonNull String string) {
        if (string.isEmpty())
            return null;

        final @NonNull Intent sendIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, string)
                .setType("text/plain");

        return Intent.createChooser(sendIntent, null);
    }

    public static @NonNull Intent getRouteIntent(final @NonNull Uri uri) {
        final @NonNull Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri)
            .setPackage("com.google.android.apps.maps");

        return mapIntent;
    }

    public static void onHelpClick(final @NonNull View v) {
        Snackbar.make(v,
                R.string.help2,
                Snackbar.LENGTH_LONG)
//                .setAction(R.string.history, (v1 -> {}))
        .show();
    }

    /* */
}
