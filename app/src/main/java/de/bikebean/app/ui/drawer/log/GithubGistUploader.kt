package de.bikebean.app.ui.drawer.log

import android.content.Context
import de.bikebean.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        private val mLogTsv: String) {

    private val mUrl: String = context.getString(R.string.url_github_gists)
    private val githubGistsToken: String = context.getString(R.string.github_gist_token)

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            mUploadSuccessNotifier(doInBackground())
        }
    }

    private suspend fun doInBackground(): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val response: Response = OkHttpClient()
                        .newBuilder()
                        .build()
                        .newCall(buildRequest())
                        .execute()

                if (response.isSuccessful) {
                    mLogViewModel.d("Successfully uploaded Crash Log (${response.code})")
                    true
                } else {
                    mLogViewModel.d("Could not upload Crash Log (${response.code}): "
                            + "${response.body?.string()}"
                    )
                    false
                }
            }

            catch (e: IOException) {
                mLogViewModel.e("Could not upload Crash Log: $e")
                false
            }
        }

    private fun buildRequest(): Request {
        val request = GithubGistBodyBuilder(mDescription, mSmsTsv, mStateTsv, mLogTsv)
                .build()
                .toRequestBody(mediaType)

        return Request.Builder()
                .url(mUrl)
                .method("POST", request)
                .addHeader("Authorization", "token $githubGistsToken")
                .addHeader("Content-Type", "application/json")
                .build()
    }

    companion object {
        val mediaType = "application/json".toMediaTypeOrNull()
    }
}