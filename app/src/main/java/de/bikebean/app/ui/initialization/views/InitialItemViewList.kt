package de.bikebean.app.ui.initialization.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import de.bikebean.app.ui.drawer.status.StateViewModel
import kotlin.math.min

class InitialItemViewList(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val initialItemList: List<InitialItemView> = listOf(
            PhoneNumberInitialView(context, attrs, this),
            ReadSmsInitialView(context, attrs, this),
            WarningNumberInitialView(context, attrs, this),
            IntervalInitialView(context, attrs, this),
            PositionInitialView(context, attrs, this),
            FinishInitialView(context, attrs, this),
    )

    constructor(context: Context) : this(context, null)

    init {
        orientation = VERTICAL
        initialItemList.forEach(::addView)
    }

    fun collapseAllExceptOne(except: InitialItemView) {
        initialItemList.forEach {
            when (it) {
                except -> it.resizeWithTransition(InitialItemView.EXPANDED)
                else -> it.resizeWithTransition(InitialItemView.COLLAPSED)
            }
        }
    }

    fun setOnClickListenerCallback(
            position: Int, onClickListenerCallback: () -> Unit
    ) = onClickListenerCallback.also { initialItemList[position].onClickListenerCallback = it }

    fun setOnClickListenerSkipCallback(
            position: Int, onClickListenerSkipCallback: (initialItemView: InitialItemView) -> Unit
    ) = onClickListenerSkipCallback.also {
        initialItemList[position].onClickListenerSkipCallback = it
    }

    fun getAdditionalElements(position: Int): View? =
            initialItemList[position].itemAdditionalLayout

    private var activeItem: Int = LIST_SIZE
        set(value) {
            field = when (value) {
                LIST_SIZE -> value
                else -> min(activeItem, value)
            }
        }

    private val doneStateList = MutableList(LIST_SIZE) {
        InitialItemView.NOT_CONFIGURED
    }

    private val activeFutureMap = mapOf(
            0 to 1..5,
            1 to 2..5,
            2 to 5..5,
            3 to 5..5,
            4 to 5..5,
            5 to null
    )

    private fun markActive(position: Int) =
            InitialItemView.ACTIVE.let { doneStateList[position] = it }

    private fun markChecked(position: Int) =
            InitialItemView.CHECKED.let { doneStateList[position] = it }

    fun skip(position: Int) = skip(initialItemList[position])

    fun skip(initialItemView: InitialItemView) {
        initialItemView.userSkip = true
    }

    fun updateChecked(st: StateViewModel) {
        activeItem = LIST_SIZE

        initialItemList.forEach {
            if (!it.userSkip && it.checkIfActive(st)) {
                activeItem = it.position
                markActive(it.position)
            } else
                markChecked(it.position)
        }

        activeFutureMap[activeItem]?.forEach {
            doneStateList[it] = InitialItemView.FUTURE
        }

        applyDoneStateList()
    }

    private fun applyDoneStateList() = initialItemList.forEachIndexed { index, item ->
        item.doneState = doneStateList[index]
    }.also {
        collapseFinishedAndEnlargeNext(activeItem - 1)
    }

    private fun collapseFinishedAndEnlargeNext(position: Int) {
        if (position >= 0)
            initialItemList[position].clickState = InitialItemView.COLLAPSED
        if (position < LIST_SIZE-1)
            initialItemList[position + 1].clickState = InitialItemView.EXPANDED
    }

    companion object {
        const val LIST_SIZE = 6
    }
}