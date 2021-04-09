package de.bikebean.app.ui.drawer.preferences

import android.content.Context
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.log.LogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class VersionChecker(
        context: Context,
        private val mLogViewModel: LogViewModel,
        private val mPreferencesViewModel: PreferencesViewModel,
        private val mNewerVersionNotifier: (VersionJsonParser.AppRelease) -> Unit
        ) {

    private val mUrl: String = context.getString(R.string.url_github_releases)
    private val githubGistsToken: String = context.getString(R.string.github_gist_token)
    private val request = Request.Builder()
            .url(mUrl)
            .method("GET", null)
            .addHeader("Authorization", "token $githubGistsToken")
            .addHeader("Content-Type", "application/json")
            .build()

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            onPostExecute(doInBackground())
        }
    }

    private suspend fun doInBackground(): VersionJsonParser =
        withContext(Dispatchers.IO) {
            val response = buildResponse() ?: return@withContext VersionJsonParser()

            response.body ?: VersionJsonParser()

            try {
                VersionJsonParser(response.body!!.string())
            } catch (e: IOException) {
                mLogViewModel.w("Could not parse Github Releases: ${e.message}")
                VersionJsonParser()
            }
        }

    private suspend fun buildResponse() : Response? =
            withContext(Dispatchers.IO) {
                try {
                    OkHttpClient()
                            .newBuilder()
                            .build()
                            .newCall(request)
                            .execute()
                            .also {
                                mLogViewModel.d("Successfully retrieved Github Releases (${it.code})")
                            }
                } catch (e: IOException) {
                    mLogViewModel.w("Could not get Github Releases: ${e.message}")
                    null
                }
            }

    private fun onPostExecute(versionJsonParser: VersionJsonParser) {
        if (!versionJsonParser.checkIfExistsNewerVersion()) {
            mPreferencesViewModel.setNewVersion("n/a")
            return
        }
        mPreferencesViewModel.setNewVersion(versionJsonParser.newAppRelease)
        mNewerVersionNotifier(versionJsonParser.newAppRelease)
    }

}