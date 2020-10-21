package de.bikebean.app.ui.drawer.preferences;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.IOException;

import de.bikebean.app.R;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VersionChecker extends AsyncTask<String, Void, VersionJsonParser> {

    private final LogViewModel mLogViewModel;
    private final PreferencesViewModel mPreferencesViewModel;
    private final @NonNull NewerVersionNotifier mNewerVersionNotifier;

    private final @NonNull String mUrl;
    private final @NonNull String githubGistsToken;

    public interface NewerVersionNotifier {
        void notifyNewerVersion(final @NonNull Release release);
    }

    public VersionChecker(final @NonNull Context context, LogViewModel lv, PreferencesViewModel pv,
                          final @NonNull NewerVersionNotifier nvn) {
        mLogViewModel = lv;
        mNewerVersionNotifier = nvn;
        mPreferencesViewModel = pv;

        mUrl = context.getString(R.string.github_releases_baseurl);
        githubGistsToken = context.getString(R.string.github_gist_token);
    }

    @Override
    protected @NonNull VersionJsonParser doInBackground(final @NonNull String... args) {
        final @NonNull OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        final @NonNull Request request = new Request.Builder()
                .url(mUrl)
                .method("GET", null)
                .addHeader("Authorization", "token " + githubGistsToken)
                .addHeader("Content-Type", "application/json")
                .build();

        final @NonNull Response response;
        final @NonNull VersionJsonParser versionJsonParser;

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
    protected void onPostExecute(final @NonNull VersionJsonParser versionJsonParser) {
        if (!versionJsonParser.checkIfExistsNewerVersion()) {
            mPreferencesViewModel.setNewVersion("n/a");
            return;
        }

        mPreferencesViewModel.setNewVersion(versionJsonParser.getNewRelease());
        mNewerVersionNotifier.notifyNewerVersion(versionJsonParser.getNewRelease());
    }
}
