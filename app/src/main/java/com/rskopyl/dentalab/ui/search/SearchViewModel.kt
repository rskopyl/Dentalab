package com.rskopyl.dentalab.ui.search

import androidx.lifecycle.viewModelScope
import com.rskopyl.dentalab.repository.OrderSearchRepository
import com.rskopyl.dentalab.util.IntentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: OrderSearchRepository
) : IntentViewModel<SearchState, SearchEvent, SearchIntent>(
    initialState = SearchState()
) {
    private val filterToRequest = mapOf(
        SearchInput.Filter.PATIENT to searchRepository::getByPatient,
        SearchInput.Filter.CLINIC to searchRepository::getByClinic,
        SearchInput.Filter.DOCTOR to searchRepository::getByDoctor
    )

    override suspend fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Search -> search(intent.input)
            is SearchIntent.ShowOrderDetails ->
                showOrderDetails(intent.orderId)
        }
    }

    private fun search(input: SearchInput) {
        val request = filterToRequest.getValue(input.filter)
        viewModelScope.launch {
            request(input.text).collectLatest { orders ->
                _state.update { state ->
                    state.copy(result = orders)
                }
            }
        }
    }

    private fun showOrderDetails(orderId: Long) {
        _events.trySend(SearchEvent.NavigateToOrderDetailsScreen(orderId))
    }
}