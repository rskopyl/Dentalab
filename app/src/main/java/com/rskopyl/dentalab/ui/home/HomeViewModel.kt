package com.rskopyl.dentalab.ui.home

import androidx.lifecycle.viewModelScope
import com.rskopyl.dentalab.data.model.Order
import com.rskopyl.dentalab.data.preferences.AppPreferencesManager
import com.rskopyl.dentalab.repository.OrderRepository
import com.rskopyl.dentalab.util.IntentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val appPreferencesRepository: AppPreferencesManager
) : IntentViewModel<HomeState, HomeEvent, HomeIntent>(
    initialState = HomeState()
) {
    init {
        intents.trySend(
            HomeIntent.LoadOrders(
                Clock.System.todayIn(TimeZone.currentSystemDefault())
            )
        )
    }

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadDates -> loadDates(intent.date)
            is HomeIntent.LoadOrders -> loadOrders(intent.date)
            is HomeIntent.ShowOrderDetails -> showOrderDetails(intent.orderId)
            is HomeIntent.CreateOrder -> createOrder()
        }
    }

    private fun loadDates(date: LocalDate) {
        viewModelScope.launch {
            appPreferencesRepository.get()
                .map { it.homePeriod }
                .distinctUntilChanged()
                .collectLatest { period ->
                    _state.update { state ->
                        state.copy(
                            dates = period.centeredAt(date),
                            selectedDate = date
                        )
                    }
                }
        }
    }

    private suspend fun loadOrders(date: LocalDate) {
        handleIntent(HomeIntent.LoadDates(date))
        viewModelScope.launch {
            orderRepository.getByDate(date).collectLatest { orders ->
                _state.update { state ->
                    state.copy(orders = orders.sortedBy { it.dateTime })
                }
            }
        }
    }

    private fun showOrderDetails(orderId: Long) {
        _events.trySend(
            HomeEvent.NavigateToOrderScreen(
                orderId = orderId, epochDays = 0
            )
        )
    }

    private fun createOrder() {
        _events.trySend(
            HomeEvent.NavigateToOrderScreen(
                orderId = Order.EMPTY_ID,
                epochDays = _state.value.selectedDate?.toEpochDays() ?: 0
            )
        )
    }
}