package de.bikebean.app.ui.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import de.bikebean.app.R;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VersionChecker extends AsyncTask<String, Void, Boolean> {

    private final LogViewModel mLogViewModel;
    private final NewerVersionNotifier mNewerVersionNotifier;

    private final String mUrl;
    private final String githubGistsToken;

    private JSONArray jsonResponse;

    public interface NewerVersionNotifier {
        void notifyNewerVersion(String name, String url);
    }

    public VersionChecker(Context context, LogViewModel lv, NewerVersionNotifier nvn) {
        mLogViewModel = lv;
        mNewerVersionNotifier = nvn;

        mUrl = "https://api.github.com/repos/bike-bean/BikebeanApp/releases";
        githubGistsToken = context.getResources().getString(R.string.github_gist_token);
    }

    @Override
    protected Boolean doInBackground(String... args) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(mUrl)
                .method("GET", null)
                .addHeader("Authorization", "token " + githubGistsToken)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
            mLogViewModel.d("Successfully retrieved Github Releases (" + response.code() + ")");
        } catch (IOException e) {
            mLogViewModel.w("Could not get Github Releases: " + e.getMessage());
            return false;
        }

        try {
            if (response.body() == null)
                return false;

            jsonResponse = new JSONArray(response.body().string());
        } catch (JSONException e) {
            mLogViewModel.w("Could not parse Github Releases JSON: " + e.getMessage());
            return false;
        } catch (IOException e) {
            mLogViewModel.w("Could not parse Github Releases: " + e.getMessage());
            return false;
        }

        return checkIfExistsNewerVersion();
    }

    private Date appDate = new Date(1);
    private Date newDate;
    private String newVersionName;
    private String newVersionUrl;

    private JSONObject newRelease;

    private boolean checkIfExistsNewerVersion() {
        Date[] dates;

        try {
            dates = getDates();
        } catch (JSONException e) {
            return false;
        }

        if (appDate.equals(new Date(1)))
            return false;

        for (Date date : dates)
            if (appDate.before(date))
                newDate = date;

        if (newDate == null)
            return false;

        try {
            newRelease = getNewRelease();
        } catch (JSONException e) {
            return false;
        }

        if (newRelease == null)
            return false;

        try {
            newVersionName = getName();
            newVersionUrl = getUrl();
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    private Date[] getDates() throws JSONException {
        Date[] ret = new Date[jsonResponse.length()];

        for (int i = 0; i < jsonResponse.length(); i++) {
            JSONObject row = jsonResponse.getJSONObject(i);

            String name = row.getString("tag_name");
            Date date = Utils.getDateFromUTCString(row.getString("created_at"));
            ret[i] = date;

            if (name.equals(Utils.getVersionName()))
                appDate = date;
        }

        return ret;
    }

    private JSONObject getNewRelease() throws JSONException {
        for (int i = 0; i < jsonResponse.length(); i++) {
            JSONObject row = jsonResponse.getJSONObject(i);

            Date date = Utils.getDateFromUTCString(row.getString("created_at"));

            if (newDate.equals(date))
                return row;
        }

        return null;
    }

    private String getUrl() throws JSONException {
        JSONArray assets = newRelease.getJSONArray("assets");
        return assets.getJSONObject(0).getString("browser_download_url");
    }

    private String getName() throws JSONException {
        return newRelease.getString("tag_name");
    }

    @Override
    protected void onPostExecute(Boolean existsNewerVersion) {
        if (!existsNewerVersion)
            return;

        mNewerVersionNotifier.notifyNewerVersion(
                newVersionName, newVersionUrl
        );
    }
}
