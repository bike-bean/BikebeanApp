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
    private final String mSmsCsv;
    private final String mStateCsv;
    private final String mLogCsv;

    private final String mUrl;
    private final String githubGistsToken;

    public interface UploadSuccessNotifier {
        void notifyUploadSuccess(boolean success);
    }

    public GithubGistUploader(Context context, LogViewModel lv, UploadSuccessNotifier usn,
                              String description, String smsCsv, String stateCsv, String logCsv) {
        mLogViewModel = lv;
        mUploadSuccessNotifier = usn;

        mDescription = description;
        mSmsCsv = smsCsv;
        mStateCsv = stateCsv;
        mLogCsv = logCsv;

        githubGistsToken = context.getResources().getString(R.string.github_gist_token);
        mUrl = "https://api.github.com/gists?public=false";
    }

    @Override
    protected Boolean doInBackground(String... args) {
        MediaType mediaType = MediaType.parse("application/json");

        GithubGistBody jsonBody = new GithubGistBody(mDescription, mSmsCsv, mStateCsv, mLogCsv);
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
