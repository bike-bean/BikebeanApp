package de.bikebean.app.ui.drawer.status.location

import android.content.Context
import de.bikebean.app.R
import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.settings.settings.add_to_list_settings.location_settings.Location
import de.bikebean.app.db.type.types.LocationType
import de.bikebean.app.ui.drawer.log.LogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

internal class LocationUpdater(
        context: Context,
        private val mStateViewModel: LocationStateViewModel,
        private val mLogViewModel: LogViewModel,
        private val mPostResponseHandler: (LocationType?) -> Unit,
        private val mWappState: WappState) {

    private val mUrl: String = context.getString(R.string.url_geolocation) +
            context.getString(R.string.google_maps_api_key)

    fun execute() {
        CoroutineScope(Dispatchers.Main).launch {
            mPostResponseHandler(doInBackground())
        }
    }

    private suspend fun doInBackground(): LocationType? =
        withContext(Dispatchers.IO) {
            val request = buildRequest() ?: return@withContext null

            if (mStateViewModel.getLocationByIdSync(mWappState))
                return@withContext null

            mStateViewModel.insertNumberStates(mWappState)
            mLogViewModel.d("Updating Coordinates (Lat/Lng)...")

            val response = try {
                OkHttpClient()
                        .newCall(request)
                        .execute()
                        .also {
                            mLogViewModel.d("Successfully posted to geolocation API (${it.code})")
                        }
            } catch (e: IOException) {
                mLogViewModel.w("Could not reach geolocation API: ${e.message}")
                return@withContext null
            }

            /*
             Mark location is updating in the UI (if it is the newest)
             */
            if (mWappState.getIfNewest(mStateViewModel))
                mStateViewModel.insert(Location(mWappState))

            try {
                val responseBody = buildResponseObject(response) ?: return@withContext null
                LocationType(responseBody, mWappState)
            } catch (e: IOException) {
                mLogViewModel.w("Could not parse Geolocation: ${e.message}")
                null
            }
        }

    private fun buildResponseObject(response: Response): ResponseBody? {
        response.body ?: return null.also {
            mLogViewModel.e("No body from Geolocation API!")
        }

        val responseBodyString = response.body!!.string()
        mLogViewModel.d("RESPONSE FROM SERVER: $responseBodyString")

        return Json.decodeFromString<ResponseBody>(responseBodyString)
    }

    private fun buildRequest(): Request? {
        val requestBodyString = LocationApiBodyCreator(mWappState, mLogViewModel).create().apply {
            when {
                isEmpty() -> return null
            }
        }

        val requestBody: RequestBody =
                when (JSON) {
                    null -> return null
                    else -> requestBodyString.toRequestBody(JSON)
                }

        return Request.Builder()
                .url(mUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()
    }

    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    }

}