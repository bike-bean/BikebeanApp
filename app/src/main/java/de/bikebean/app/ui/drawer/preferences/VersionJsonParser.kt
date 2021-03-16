package de.bikebean.app.ui.drawer.preferences

import de.bikebean.app.ui.utils.date.DateUtils.getDateFromUTCString
import de.bikebean.app.ui.utils.device.DeviceUtils.versionName
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.util.*

class VersionJsonParser {

    val newAppRelease: AppRelease

    private val githubReleases: List<Release>
    private val isNewRelease: Boolean

    constructor(jsonResponseString: String) {
        githubReleases = Json { ignoreUnknownKeys = true }
                .decodeFromString(jsonResponseString)

        val dates = dates
        val appDate = appDate
        val newDate: Date

        if (appDate.before(dates.first()) && appDate != Date(1))
            newDate = dates.first()
        else {
            newAppRelease = AppRelease()
            isNewRelease = false
            return
        }

        val newRelease = getNewReleaseByDate(newDate)
        if (newRelease == null) {
            newAppRelease = AppRelease()
            isNewRelease = false
            return
        }

        newAppRelease = AppRelease(
                newRelease.tag_name,
                newRelease.assets[0].browser_download_url
        )
        isNewRelease = true
    }

    constructor() {
        githubReleases = listOf()
        isNewRelease = false
        newAppRelease = AppRelease()
    }

    fun checkIfExistsNewerVersion(): Boolean {
        return isNewRelease
    }

    private val dates: List<Date>
        get() = List(githubReleases.size) {
            getDateFromUTCString(githubReleases[it].created_at)
        }

    private val appDate: Date
        get() = githubReleases.firstOrNull { it.tag_name == versionName }?.let {
            getDateFromUTCString(it.created_at)
        } ?: Date(1)

    private fun getNewReleaseByDate(newDate: Date): Release? =
            githubReleases.firstOrNull {
                getDateFromUTCString(it.created_at) == newDate
            }

    @Serializable
    data class Release(
            val assets: List<Asset>,
            val created_at: String,
            val tag_name: String
    )

    @Serializable
    data class Asset(
            val browser_download_url: String
    )

    data class AppRelease(
            val name: String,
            val url: String
    ) {
        constructor() : this("", "")
    }
}

