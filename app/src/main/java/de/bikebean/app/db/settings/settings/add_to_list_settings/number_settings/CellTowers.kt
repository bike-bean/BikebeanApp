package de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings

import de.bikebean.app.db.settings.settings.add_to_list_settings.NumberSetting
import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.state.StateFactory
import kotlinx.serialization.Serializable

class CellTowers : NumberSetting {

    override val list: List<CellTower>

    constructor(sms: Sms, cellTowers: String) :
            super(cellTowers, sms, key,
                    StateFactory.createNumberState(
                            sms, numberKey,
                            getNumberFromString(cellTowers).toDouble(),
                            State.STATUS.CONFIRMED)
            ) {
        list = CellTowerListBuilder(cellTowers)
            }

    constructor(sms: Sms, cellTowersGetter: () -> String) :
            this(sms, cellTowersGetter())

    constructor(wappState: WappState) :
            this(wappState.sms, wappState.cellTowers.longValue)

    constructor() :
            super("", key, StateFactory.createNullState()
            ) {
        list = CellTowerListBuilder("")
            }

    class CellTowerListBuilder(stringArrayWapp: String) : ArrayList<CellTower>() {

        init {
            if (stringArrayWapp.isNotEmpty())
                stringArrayWapp.split("\n").forEach { s ->
                    add(CellTowerBuilder(s).get())
                }
        }

        fun add(element: CellTower?): Boolean {
            return when (element) {
                null -> false
                else -> super.add(element)
            }
        }

    }

    @Serializable
    data class CellTower(
            val mobileCountryCode : Int,
            val mobileNetworkCode : Int,
            val locationAreaCode : Int,
            val cellId : Int,
            val signalStrength : Int)
        : RawNumberSettings()

    class CellTowerBuilder(s: String) {

        private val cellTower: CellTower? = when {
            s == "    " || s.isEmpty() -> null
            else -> {
                val stringArrayGsmTowers = s.split(",")

                CellTower(
                        stringArrayGsmTowers[0].toInt(),
                        stringArrayGsmTowers[1].toInt(),
                        stringArrayGsmTowers[2].toInt(16),
                        stringArrayGsmTowers[3].toInt(16),
                        ("-" + stringArrayGsmTowers[4]).toInt()
                )
            }
        }

        fun get() : CellTower? = cellTower

    }

    companion object {
        private val key = KEY.CELL_TOWERS
        private val numberKey = KEY.NO_CELL_TOWERS
    }
}