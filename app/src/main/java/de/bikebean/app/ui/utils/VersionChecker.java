package de.bikebean.app.ui.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

import de.bikebean.app.R;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.preferences.PreferencesViewModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VersionChecker extends AsyncTask<String, Void, VersionJsonParser> {

    private final LogViewModel mLogViewModel;
    private final PreferencesViewModel mPreferencesViewModel;
    private final NewerVersionNotifier mNewerVersionNotifier;

    private final String mUrl;
    private final String githubGistsToken;

    public interface NewerVersionNotifier {
        void notifyNewerVersion(Release release);
    }

    public VersionChecker(Context context, LogViewModel lv, PreferencesViewModel pv,
                          NewerVersionNotifier nvn) {
        mLogViewModel = lv;
        mNewerVersionNotifier = nvn;
        mPreferencesViewModel = pv;

        mUrl = context.getString(R.string.github_releases_baseurl);
        githubGistsToken = context.getString(R.string.github_gist_token);
    }

    @Override
    protected VersionJsonParser doInBackground(String... args) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(mUrl)
                .method("GET", null)
                .addHeader("Authorization", "token " + githubGistsToken)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response;
        VersionJsonParser versionJsonParser;

        try {
            response = client.newCall(request).execute();
            mLogViewModel.d("Successfully retrieved Github Releases (" + response.code() + ")");
        } catch (IOException e) {
            mLogViewModel.w("Could not get Github Releases: " + e.getMessage());
            return new VersionJsonParser();
        }

        try {
            if (response.body() == null)
                return new VersionJsonParser();

            versionJsonParser = new VersionJsonParser(response.body().string());
        } catch (JSONException e) {
            mLogViewModel.w("Could not parse Github Releases JSON: " + e.getMessage());
            return new VersionJsonParser();
        } catch (IOException e) {
            mLogViewModel.w("Could not parse Github Releases: " + e.getMessage());
            return new VersionJsonParser();
        }

        return versionJsonParser;
    }

    @Override
    protected void onPostExecute(VersionJsonParser versionJsonParser) {
        if (!versionJsonParser.checkIfExistsNewerVersion()) {
            mPreferencesViewModel.setNewVersion("n/a");
            return;
        }

        mPreferencesViewModel.setNewVersion(versionJsonParser.getNewRelease());
        mNewerVersionNotifier.notifyNewerVersion(versionJsonParser.getNewRelease());
    }
}
