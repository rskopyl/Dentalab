package com.rskopyl.dentalab.ui.home

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.datepicker.MaterialDatePicker
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.databinding.FragmentHomeBinding
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.SpacingItemDecoration
import com.rskopyl.dentalab.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), MenuProvider {

    private val viewModel:
            IntentViewModel<HomeState, HomeEvent, HomeIntent>
            by viewModels<HomeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val dateAdapter = LocalDateAdapter(
        onItemClick = { date ->
            viewModel.intents.trySend(
                HomeIntent.LoadOrders(date)
            )
        }
    )
    private val orderAdapter = OrderAdapter(
        onItemClick = { order ->
            viewModel.intents.trySend(
                HomeIntent.ShowOrderDetails(order.id)
            )
        }
    )

    private val centerSmoothScroller by lazy {
        CenterSmoothScroller(requireContext())
    }

    private fun setupUi() = binding.run {
        (activity as? AppCompatActivity)?.supportActionBar?.run {
            val spannableString = SpannableString(getString(R.string.app_name))
            val spanColor = ContextCompat.getColor(
                requireContext(), R.color.brand
            )
            spannableString.setSpan(
                ForegroundColorSpan(spanColor),
                5, spannableString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            title = spannableString
        }
        rvDates.run {
            adapter = dateAdapter
            setHasFixedSize(true)
            LinearSnapHelper().attachToRecyclerView(this)
            addItemDecoration(
                SpacingItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.small_100)
                )
            )
        }
        rvOrders.run {
            adapter = orderAdapter
            setHasFixedSize(true)
            addItemDecoration(
                SpacingItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.small_175)
                )
            )
        }
        fabCreate.setOnClickListener {
            viewModel.intents.trySend(HomeIntent.CreateOrder)
        }
    }

    private fun handleState(state: HomeState) {
        dateAdapter.run {
            submitDates(state.dates)
            submitSelectedDate(state.selectedDate)
        }
        orderAdapter.submitList(state.orders)
        binding.run {
            tvEmpty.isVisible = state.orders.isEmpty()
            rvDates.doOnNextLayout {
                val itemCount = dateAdapter.itemCount
                if (itemCount < 2) return@doOnNextLayout
                rvDates.layoutManager?.startSmoothScroll(
                    centerSmoothScroller.apply {
                        targetPosition = itemCount / 2
                    }
                )
            }
        }
    }

    private fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.NavigateToOrderScreen -> {
                HomeFragmentDirections
                    .actionHomeFragmentToOrderFragment(
                        orderId = event.orderId, epochDays = event.epochDays
                    )
                    .let { findNavController().navigate(it) }
            }
        }
    }

    private fun showDatePicker() {
        MaterialDatePicker.Builder.datePicker().build()
            .apply {
                addOnPositiveButtonClickListener { millis ->
                    val date = Instant.fromEpochMilliseconds(millis)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                    viewModel.intents.trySend(HomeIntent.LoadOrders(date))
                }
            }
            .show(childFragmentManager, null)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
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

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.mi_pick_date -> showDatePicker()
            else -> return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}