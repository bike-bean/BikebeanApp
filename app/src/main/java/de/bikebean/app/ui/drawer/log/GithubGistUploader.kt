package de.bikebean.app.ui.drawer.log

import android.content.Context
import android.os.AsyncTask
import de.bikebean.app.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class GithubGistUploader(
        context: Context,
        private val mLogViewModel: LogViewModel,
        private val mUploadSuccessNotifier: (Boolean) -> Unit,
        private val mDescription: String,
        private val mSmsTsv: String,
        private val mStateTsv: String,
        private val mLogTsv: String) : AsyncTask<String, Void?, Boolean>() {

    private val mUrl: String = context.getString(R.string.url_github_gists)
    private val githubGistsToken: String = context.getString(R.string.github_gist_token)

    override fun doInBackground(vararg args: String): Boolean = try {
        val response: Response = OkHttpClient()
                .newBuilder()
                .build()
                .newCall(buildRequest())
                .execute()

        if (response.isSuccessful) {
            mLogViewModel.d("Successfully uploaded Crash Log (${response.code})")
            true
        } else {
            mLogViewModel.d(
                    "Could not upload Crash Log (${response.code}): ${response.body?.string()}"
            )
            false
        }
    } catch (e: IOException) {
        mLogViewModel.e("Could not upload Crash Log: $e")
        false
    }

    private fun buildRequest(): Request {
        val request = GithubGistBodyBuilder(mDescription, mSmsTsv, mStateTsv, mLogTsv).build()
                .toRequestBody(mediaType)

        return Request.Builder()
                .url(mUrl)
                .method("POST", request)
                .addHeader("Authorization", "token $githubGistsToken")
                .addHeader("Content-Type", "application/json")
                .build()
    }

    override fun onPostExecute(isLogUploaded: Boolean) {
        mUploadSuccessNotifier(isLogUploaded)
    }

    companion object {
        val mediaType = "application/json".toMediaTypeOrNull()
    }
}