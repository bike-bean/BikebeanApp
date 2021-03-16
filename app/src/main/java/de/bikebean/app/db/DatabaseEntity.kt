package de.bikebean.app.db

abstract class DatabaseEntity {
    abstract val nullType: DatabaseEntity?
    abstract fun createReportTitle(): String?
    abstract fun createReport(): String?
}