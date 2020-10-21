package de.bikebean.app.ui.drawer.preferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import de.bikebean.app.ui.utils.date.DateUtils;
import de.bikebean.app.ui.utils.device.DeviceUtils;

public class VersionJsonParser {

    private static final String APK_ALT_DL_URL = "https://bike-bean.de/software/";

    private static final @NonNull String JSON_DATE = "created_at";
    private static final @NonNull String JSON_NAME = "tag_name";
    private static final @NonNull String JSON_URL = "browser_download_url";

    private final @NonNull JSONArray jsonResponse;
    private final @NonNull JSONObject newReleaseJson;

    private final boolean isNewRelease;
    private final @NonNull Release newRelease;

    public VersionJsonParser(@NonNull String responseBody) throws JSONException {
        final @NonNull Date[] dates;
        final @NonNull Date appDate;
        final @NonNull Date newDate;

        final @NonNull Release tmpRelease;
        final @Nullable JSONObject tmpReleaseJson;

        jsonResponse = new JSONArray(responseBody);

        try {
            dates = getDates();
            appDate = getAppDate();
        } catch (JSONException e) {
            newReleaseJson = new JSONObject();
            newRelease = new Release();
            isNewRelease = false;
            return;
        }

        if (appDate.before(dates[0]) && !appDate.equals(new Date(1)))
            newDate = dates[0];
        else {
            newReleaseJson = new JSONObject();
            newRelease = new Release();
            isNewRelease = false;
            return;
        }

        try {
            tmpReleaseJson = getNewRelease(newDate);
        } catch (JSONException e) {
            newReleaseJson = new JSONObject();
            newRelease = new Release();
            isNewRelease = false;
            return;
        }

        if (tmpReleaseJson == null) {
            newReleaseJson = new JSONObject();
            newRelease = new Release();
            isNewRelease = false;
            return;
        }

        newReleaseJson = tmpReleaseJson;

        try {
            tmpRelease = new Release(getName(), getUrl());
        } catch (JSONException e) {
            newRelease = new Release();
            isNewRelease = false;
            return;
        }

        newRelease = tmpRelease;
        isNewRelease = true;
    }

    public VersionJsonParser() {
        jsonResponse = new JSONArray();
        newReleaseJson = new JSONObject();

        isNewRelease = false;
        newRelease = new Release();
    }

    public boolean checkIfExistsNewerVersion() {
        return isNewRelease;
    }

    public @NonNull Release getNewRelease() {
        return newRelease;
    }

    private @NonNull Date[] getDates() throws JSONException {
        final @NonNull Date[] ret = new Date[jsonResponse.length()];

        for (int i = 0; i < jsonResponse.length(); i++) {
            ret[i] = DateUtils.getDateFromUTCString(
                    jsonResponse.getJSONObject(i).getString(JSON_DATE)
            );
        }

        return ret;
    }

    private @NonNull Date getAppDate() throws JSONException {
        for(int i = 0; i < jsonResponse.length(); i++) {
            final @NonNull JSONObject row = jsonResponse.getJSONObject(i);

            if (row.get("tag_name").equals(DeviceUtils.getVersionName()))
                return DateUtils.getDateFromUTCString(row.getString(JSON_DATE));
        }

        return new Date(1);
    }

    private @Nullable JSONObject getNewRelease(Date newDate) throws JSONException {
        for (int i = 0; i < jsonResponse.length(); i++) {
            final @NonNull JSONObject row = jsonResponse.getJSONObject(i);

            final @NonNull Date date =
                    DateUtils.getDateFromUTCString(row.getString(JSON_DATE));

            if (newDate.equals(date))
                return row;
        }

        return null;
    }

    private @NonNull String getUrl() throws JSONException {
        final @NonNull JSONArray assets = newReleaseJson.getJSONArray("assets");

        /* This URL points directly to the APK from GITHUB which is very convenient but MAY have
           introduced a false positive virus report (probably due to suspicion of
           APK side-loading which is of course bad, but not what we do)

           Anyway, as this is not confirmed, just point to the Homepage URL for the time being,
           until the virus report is clarified.
         */
        // return assets.getJSONObject(0).getString("browser_download_url");

        /* Add useless check to make AndroidStudio quiet */
        if (!assets.getJSONObject(0).getString(JSON_URL).equals(""))
            return APK_ALT_DL_URL;
        else
            return "";
    }

    private @NonNull String getName() throws JSONException {
        return newReleaseJson.getString(JSON_NAME);
    }
}
