package com.rskopyl.dentalab.ui.home

import com.rskopyl.dentalab.data.model.Order
import kotlinx.datetime.LocalDate

data class HomeState(
    val dates: List<LocalDate> = emptyList(),
    val selectedDate: LocalDate? = null,
    val orders: List<Order> = emptyList()
)

sealed interface HomeEvent {

    data class NavigateToOrderScreen(
        val orderId: Long, val epochDays: Int
    ) : HomeEvent
}

sealed interface HomeIntent {

    data class LoadDates(val date: LocalDate) : HomeIntent
    data class LoadOrders(val date: LocalDate) : HomeIntent
    data class ShowOrderDetails(val orderId: Long) : HomeIntent
    object CreateOrder : HomeIntent
}