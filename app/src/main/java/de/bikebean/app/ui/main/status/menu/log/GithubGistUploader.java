package de.bikebean.app.ui.main.status.menu.log;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import de.bikebean.app.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GithubGistUploader extends AsyncTask<String, Void, Boolean> {

    private final LogViewModel mLogViewModel;
    private final UploadSuccessNotifier mUploadSuccessNotifier;

    private final String mDescription;
    private final String mSmsTsv;
    private final String mStateTsv;
    private final String mLogTsv;

    private final String mUrl;
    private final String githubGistsToken;

    public interface UploadSuccessNotifier {
        void notifyUploadSuccess(boolean success);
    }

    public GithubGistUploader(Context context, LogViewModel lv, UploadSuccessNotifier usn,
                              String description, String smsTsv, String stateTsv, String logTsv) {
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
    protected Boolean doInBackground(String... args) {
        MediaType mediaType = MediaType.parse("application/json");

        GithubGistBody jsonBody = new GithubGistBody(mDescription, mSmsTsv, mStateTsv, mLogTsv);
        RequestBody body = RequestBody.create(mediaType, jsonBody.createJsonApiBody());

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(mUrl)
                .method("POST", body)
                .addHeader("Authorization", "token " + githubGistsToken)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response;

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
    protected void onPostExecute(Boolean isLogUploaded) {
        mUploadSuccessNotifier.notifyUploadSuccess(isLogUploaded);
    }
}
