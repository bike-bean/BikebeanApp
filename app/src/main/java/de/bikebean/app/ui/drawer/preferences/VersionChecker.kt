package de.bikebean.app.ui.drawer.preferences

import android.content.Context
import android.os.AsyncTask
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.log.LogViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class VersionChecker(
        context: Context,
        private val mLogViewModel: LogViewModel,
        private val mPreferencesViewModel: PreferencesViewModel,
        private val mNewerVersionNotifier: (VersionJsonParser.AppRelease) -> Unit
        ) : AsyncTask<String, Void?, VersionJsonParser>() {

    private val mUrl: String = context.getString(R.string.github_releases_baseurl)
    private val githubGistsToken: String = context.getString(R.string.github_gist_token)
    private val request = Request.Builder()
            .url(mUrl)
            .method("GET", null)
            .addHeader("Authorization", "token $githubGistsToken")
            .addHeader("Content-Type", "application/json")
            .build()

    override fun doInBackground(vararg args: String): VersionJsonParser {
        val response = buildResponse() ?: return VersionJsonParser()

        response.body ?: return VersionJsonParser()

        return try {
            VersionJsonParser(response.body!!.string())
        } catch (e: IOException) {
            mLogViewModel.w("Could not parse Github Releases: ${e.message}")
            VersionJsonParser()
        }
    }

    private fun buildResponse() : Response? = try {
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

    override fun onPostExecute(versionJsonParser: VersionJsonParser) {
        if (!versionJsonParser.checkIfExistsNewerVersion()) {
            mPreferencesViewModel.setNewVersion("n/a")
            return
        }
        mPreferencesViewModel.setNewVersion(versionJsonParser.newAppRelease)
        mNewerVersionNotifier(versionJsonParser.newAppRelease)
    }

}