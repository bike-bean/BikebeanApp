package de.bikebean.app.ui.utils;

public class Release {

    private final String name;
    private final String url;

    public Release(String newVersionName, String newVersionUrl) {
        this.name = newVersionName;
        this.url = newVersionUrl;
    }

    public Release() {
        this.name = "";
        this.url = "";
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
