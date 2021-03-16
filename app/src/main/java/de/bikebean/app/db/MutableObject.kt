package de.bikebean.app.db

class MutableObject {

    @Volatile
    private var isSet = false

    private var t: DatabaseEntity? = null
        private set(value) {
            field = value
            isSet = true
        }
        get() = when {
            isSet -> field
            else -> null
        }

    private var all: List<DatabaseEntity> = emptyList()
        private set(value) {
            field = value
            isSet = true
        }
        get() = when {
            isSet -> field
            else -> emptyList()
        }

    fun getDbEntitySync(listGetter: (String, Int) -> List<DatabaseEntity>,
                        sArg: String, iArg: Int): DatabaseEntity? {
        Thread { t = listGetter(sArg, iArg).firstOrNull() }.start()

        waitForStateChange()
        return t
    }

    fun getAllItems(allItemsGetter: () -> List<DatabaseEntity>): List<DatabaseEntity> {
        Thread { all = allItemsGetter() }.start()

        waitForStateChangeAll()
        return all
    }

    fun waitForDelete(deleteChecker: () -> List<DatabaseEntity?>) {
        Thread { t = waitForDeleteFinished(deleteChecker) }.start()

        waitForStateChange()
    }

    private fun waitForStateChange() {
        while (!isSet) try {
            Thread.sleep(10)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun waitForStateChangeAll() {
        while (!isSet) try {
            Thread.sleep(10)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun waitForDeleteFinished(deleteChecker: () -> List<DatabaseEntity?>) : DatabaseEntity? {
        while (deleteChecker().isNotEmpty()) {
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        return null
    }
}