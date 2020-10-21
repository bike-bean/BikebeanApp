package de.bikebean.app.ui.utils.date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Period {

    private final long s;
    private final long m;
    private final int h;
    private final int d;
    private final int y;

    private final @NonNull String outputTime;
    private final @NonNull String outputDate;
    private final @NonNull String outputDateWithYear;
    private final @NonNull String outputAll;

    private final int periodMargin;

    public Period(long datetime) {
        Date now = new Date();
        Date then = new Date(datetime);

        long ms = now.getTime() - then.getTime();
        s = ms / 1000; m = s / 60;
        h = (int) m / 60; d = h / 24;
        double yy = d / 365.0;
        y = Math.round((float) yy);

        outputTime = formatDate("HH:mm", then);
        outputDate = formatDate("dd.MM", then);
        outputDateWithYear = formatDate("dd.MM.yy", then);
        outputAll = formatDate("dd.MM.yy", then);

        periodMargin = getPeriodMargin();
    }

    private static @NonNull String formatDate(final @NonNull String pattern,
                                              final @NonNull Date date) {
        return new SimpleDateFormat(pattern, Locale.GERMANY).format(date);
    }

    private int getPeriodMargin() {
        if (y > 1)
            return 1;
        if (d > 300)
            return 2;
        if (d > 1)
            return 3;
        if (d > 0)
            return 4;
        if (h > 1)
            return 5;
        if (h > 0)
            return 6;
        if (m > 1)
            return 7;
        if (m > 0)
            return 8;
        if (s > 1)
            return 9;
        if (s > 0)
            return 10;

        return 0;
    }

    public @NonNull String convertPeriodToHuman() {
        return getDateString() + getPeriodString();
    }

    public @NonNull String getLastChangedString() {
        final @NonNull String preString;
        if (periodMargin > 4)
            preString = " um " ;
        else
            preString = " am ";

        return "Zuletzt aktualisiert" + preString + getDateString() + getPeriodString();
    }

    private @NonNull String getDateString() {
        if (periodMargin >= 1 && periodMargin <= 2)
            return outputDateWithYear;
        else if (periodMargin >= 3 && periodMargin <= 4)
            return outputDate;
        else if (periodMargin >= 5)
            return outputTime;

        return outputAll;
    }

    private final @NonNull Map<Integer, String> marginMap = new HashMap<Integer, String>() {{
        put(1, " (Vor %d Jahren)");
        put(2, " (Vor 1 Jahr)");
        put(3, " (Vor %d Tagen)");
        put(4, " (Vor 1 Tag)");
        put(5, " (Vor %d Stunden)");
        put(6, " (Vor 1 Stunde)");
        put(7, " (Vor %d Minuten)");
        put(8, " (Vor 1 Minute)");
        put(9, " (Vor %d Sekunden)");
        put(10, " (Vor 1 Sekunde)");
    }};

    private @NonNull String getPeriodString() {
        final @Nullable String periodString = marginMap.get(periodMargin);

        if (periodString != null)
            return formatPeriodString(periodString);

        return "";
    }

    private @NonNull String formatPeriodString(@NonNull String periodString) {
        switch (periodMargin) {
            case 1: return formatPeriodString(periodString, y);
            case 3: return formatPeriodString(periodString, d);
            case 5: return formatPeriodString(periodString, h);
            case 7: return formatPeriodString(periodString, (int) m);
            case 9: return formatPeriodString(periodString, (int) s);
            default: return periodString;
        }
    }

    private @NonNull String formatPeriodString(@NonNull String periodString, int period) {
        return String.format(Locale.GERMANY, periodString, period);
    }
}
