package de.bikebean.app.ui.drawer.preferences;

import androidx.annotation.NonNull;

public class Release {

    private final @NonNull String name;
    private final @NonNull String url;

    public Release(final @NonNull String newVersionName, final @NonNull String newVersionUrl) {
        this.name = newVersionName;
        this.url = newVersionUrl;
    }

    public Release() {
        this.name = "";
        this.url = "";
    }

    public @NonNull String getName() {
        return name;
    }

    public @NonNull String getUrl() {
        return url;
    }
}
