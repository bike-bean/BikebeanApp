package de.bikebean.app.ui.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class VersionJsonParser {

    private static final String APK_ALT_DL_URL = "https://bike-bean.de/software/";

    private final JSONArray jsonResponse;
    private final JSONObject newReleaseJson;

    private final boolean isNewRelease;
    private final Release newRelease;

    public VersionJsonParser(String responseBody) throws JSONException {
        Date[] dates;
        Date appDate;
        Date newDate;

        Release tmpRelease;
        JSONObject tmpReleaseJson;

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

    public Release getNewRelease() {
        return newRelease;
    }

    private Date[] getDates() throws JSONException {
        Date[] ret = new Date[jsonResponse.length()];

        for (int i = 0; i < jsonResponse.length(); i++) {
            JSONObject row = jsonResponse.getJSONObject(i);

            Date date = Utils.getDateFromUTCString(row.getString("created_at"));
            ret[i] = date;
        }

        return ret;
    }

    private Date getAppDate() throws JSONException {
        for(int i = 0; i < jsonResponse.length(); i++) {
            JSONObject row = jsonResponse.getJSONObject(i);

            if (row.get("tag_name").equals(Utils.getVersionName()))
                return Utils.getDateFromUTCString(row.getString("created_at"));
        }

        return new Date(1);
    }

    private JSONObject getNewRelease(Date newDate) throws JSONException {
        for (int i = 0; i < jsonResponse.length(); i++) {
            JSONObject row = jsonResponse.getJSONObject(i);

            Date date = Utils.getDateFromUTCString(row.getString("created_at"));

            if (newDate.equals(date))
                return row;
        }

        return null;
    }

    private String getUrl() throws JSONException {
        JSONArray assets = newReleaseJson.getJSONArray("assets");

        /* This URL points directly to the APK from GITHUB which is very convenient but MAY have
           introduced a false positive virus report (probably due to suspicion of
           APK side-loading which is of course bad, but not what we do)

           Anyway, as this is not confirmed, just point to the Homepage URL for the time being,
           until the virus report is clarified.
         */
        // return assets.getJSONObject(0).getString("browser_download_url");

        /* Add useless check to make AndroidStudio quiet */
        if (!assets.getJSONObject(0).getString("browser_download_url").equals(""))
            return APK_ALT_DL_URL;
        else
            return "";
    }

    private String getName() throws JSONException {
        return newReleaseJson.getString("tag_name");
    }
}
