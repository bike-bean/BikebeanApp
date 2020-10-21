package de.bikebean.app.ui.drawer.log;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import de.bikebean.app.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GithubGistUploader extends AsyncTask<String, Void, Boolean> {

    private final LogViewModel mLogViewModel;
    private final @NonNull UploadSuccessNotifier mUploadSuccessNotifier;

    private final @NonNull String mDescription;
    private final @NonNull String mSmsTsv;
    private final @NonNull String mStateTsv;
    private final @NonNull String mLogTsv;

    private final @NonNull String mUrl;
    private final @NonNull String githubGistsToken;

    public interface UploadSuccessNotifier {
        void notifyUploadSuccess(boolean success);
    }

    public GithubGistUploader(final @NonNull Context context, LogViewModel lv,
                              final @NonNull UploadSuccessNotifier usn,
                              final @NonNull String description, final @NonNull String smsTsv,
                              final @NonNull String stateTsv, final @NonNull String logTsv) {
        mLogViewModel = lv;
        mUploadSuccessNotifier = usn;

        mDescription = description;
        mSmsTsv = smsTsv;
        mStateTsv = stateTsv;
        mLogTsv = logTsv;

        githubGistsToken = context.getString(R.string.github_gist_token);
        mUrl = context.getString(R.string.github_gists_baseurl);
    }

    @Override
    protected @NonNull Boolean doInBackground(final @NonNull String... args) {
        final @Nullable MediaType mediaType = MediaType.parse("application/json");

        final @NonNull GithubGistBody jsonBody =
                new GithubGistBody(mDescription, mSmsTsv, mStateTsv, mLogTsv);
        final @NonNull RequestBody body =
                RequestBody.create(mediaType, jsonBody.createJsonApiBody());

        final @NonNull OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        final @NonNull Request request = new Request.Builder()
                .url(mUrl)
                .method("POST", body)
                .addHeader("Authorization", "token " + githubGistsToken)
                .addHeader("Content-Type", "application/json")
                .build();

        final @NonNull Response response;

        try {
            response = client.newCall(request).execute();
            mLogViewModel.d("Successfully uploaded Crash Log (" + response.code() + ")");
            return true;
        } catch (IOException e) {
            mLogViewModel.e("Could not upload Crash Log: " + e.toString());
            return false;
        }
    }

    @Override
    protected void onPostExecute(final @NonNull Boolean isLogUploaded) {
        mUploadSuccessNotifier.notifyUploadSuccess(isLogUploaded);
    }
}
