package com.rskopyl.dentalab.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.databinding.FragmentSearchBinding
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.SpacingItemDecoration
import com.rskopyl.dentalab.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel:
            IntentViewModel<SearchState, SearchEvent, SearchIntent>
            by viewModels<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val orderSearchAdapter = OrderSearchAdapter(
        onItemClick = { order ->
            viewModel.intents.trySend(
                SearchIntent.ShowOrderDetails(order.id)
            )
        }
    )

    private fun setupUi() = binding.run {
        rvResult.run {
            adapter = orderSearchAdapter
            setHasFixedSize(true)
            addItemDecoration(
                SpacingItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.small_100)
                )
            )
        }
        tilText.setEndIconOnClickListener {
            sendSearchInput()
        }
    }

    private fun handleState(state: SearchState) {
        orderSearchAdapter.submitList(state.result)
        binding.run {
            rvResult.isVisible = state.result.isNotEmpty()
            tvEmpty.isVisible = state.result.isEmpty()
        }
    }

    private fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.NavigateToOrderDetailsScreen -> {
                SearchFragmentDirections
                    .actionSearchFragmentToOrderFragment(
                        orderId = event.orderId, epochDays = 0
                    )
                    .let { findNavController().navigate(it) }
            }
        }
    }

    private fun sendSearchInput() = binding.run {
        val input = SearchInput(
            text = etText.text?.toString() ?: "",
            filter = FILTER_CHIP_ID_TO_FILTER
                .getValue(chipsFilter.checkedChipId)
        )
        viewModel.intents.trySend(
            SearchIntent.Search(input)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        setupUi()
        viewModel.run {
            state.collectOnLifecycle(
                viewLifecycleOwner,
                collector = ::handleState
            )
            events.collectOnLifecycle(
                viewLifecycleOwner,
                collector = ::handleEvent
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {

        val FILTER_CHIP_ID_TO_FILTER = mapOf<Int, SearchInput.Filter>(
            R.id.chip_patient to SearchInput.Filter.PATIENT,
            R.id.chip_clinic to SearchInput.Filter.CLINIC,
            R.id.chip_doctor to SearchInput.Filter.DOCTOR
        )
    }
}