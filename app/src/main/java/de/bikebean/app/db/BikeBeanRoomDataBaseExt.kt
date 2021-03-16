package de.bikebean.app.db

import android.content.Context
import de.bikebean.app.ui.drawer.log.GithubGistUploader
import de.bikebean.app.ui.drawer.log.LogViewModel
import de.bikebean.app.ui.utils.date.DateUtils.convertToDateHuman
import de.bikebean.app.ui.utils.device.DeviceUtils.getUUID
import de.bikebean.app.ui.utils.device.DeviceUtils.versionName

fun resetAll() {
    val smsDao = BikeBeanRoomDatabase.INSTANCE.smsDao()
    val stateDao = BikeBeanRoomDatabase.INSTANCE.stateDao()
    val logDao = BikeBeanRoomDatabase.INSTANCE.logDao()

    BikeBeanRoomDatabase.databaseWriteExecutor.execute {
        smsDao.deleteAll()
        stateDao.deleteAll()
        logDao.deleteAll()
    }

    MutableObject().waitForDelete { smsDao.allSync }
    MutableObject().waitForDelete { stateDao.allSync }
    MutableObject().waitForDelete { logDao.allSync }
}

fun createReport(ctx: Context, lv: LogViewModel?,
                 usn: (Boolean) -> Unit): GithubGistUploader {
    val smsDao = BikeBeanRoomDatabase.INSTANCE.smsDao()
    val stateDao = BikeBeanRoomDatabase.INSTANCE.stateDao()
    val logDao = BikeBeanRoomDatabase.INSTANCE.logDao()

    val smsTsv = createReportTsv { smsDao.allSync }
    val stateTsv = createReportTsv { stateDao.allSync }
    val logTsv = createReportTsv { logDao.allSync }

    val description = """BikeBeanAppCrashReport ${convertToDateHuman()} 
Version: $versionName 
ID: ${getUUID(ctx)}"""

    return GithubGistUploader(
            ctx, lv!!, usn,
            description, smsTsv, stateTsv, logTsv
    )
}

private fun createReportTsv(listGetter: () -> List<DatabaseEntity>) : String =
        StringBuilder().apply {
            MutableObject().getAllItems(listGetter).let { list ->
                append(list.firstOrNull()?.createReportTitle() ?: "dummy" )
                list.forEach { append(it.createReport()) }
            }
        }.toString()
