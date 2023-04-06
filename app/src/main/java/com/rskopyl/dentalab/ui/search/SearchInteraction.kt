package com.rskopyl.dentalab.ui.search

import com.rskopyl.dentalab.data.model.Order

data class SearchState(
    val result: List<Order> = emptyList()
)

sealed interface SearchIntent {

    data class Search(val input: SearchInput) : SearchIntent
    data class ShowOrderDetails(val orderId: Long) : SearchIntent
}

sealed interface SearchEvent {

    data class NavigateToOrderDetailsScreen(val orderId: Long) : SearchEvent
}