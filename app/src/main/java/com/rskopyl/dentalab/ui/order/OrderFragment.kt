package com.rskopyl.dentalab.ui.order

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.data.model.Order
import com.rskopyl.dentalab.databinding.FragmentOrderBinding
import com.rskopyl.dentalab.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OrderFragment : Fragment(R.layout.fragment_order),
    InputErrorsMedia, MenuProvider {

    @Inject
    lateinit var viewModelDaggerFactory: OrderViewModel.DaggerFactory

    private val viewModel:
            IntentViewModel<OrderState, OrderEvent, OrderIntent>
            by viewModels<OrderViewModel> {
                val args = navArgs<OrderFragmentArgs>().value
                val orderId = args.orderId
                ViewModelProvider.Factory {
                    viewModelDaggerFactory.create(
                        mode = if (orderId == Order.EMPTY_ID) {
                            OrderState.Mode.Creating(args.epochDays)
                        } else {
                            OrderState.Mode.Editing(orderId)
                        }
                    )
                }
            }

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val dateFormat = SimpleDateFormat(
        "dd.MM.yyyy", Locale.getDefault()
    ).apply {
        timeZone = java.util.TimeZone.getTimeZone("UTC")
    }
    private val timeFormat = SimpleDateFormat(
        "k:mm", Locale.getDefault()
    ).apply {
        timeZone = java.util.TimeZone.getTimeZone("UTC")
    }

    private val clinicHintAdapter by lazy {
        ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )
    }
    private val doctorHintAdapter by lazy {
        ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )
    }

    override val inputFieldToErrors by lazy {
        binding.run {
            mapOf<TextInputLayout, Set<Any>>(
                tilPatient to setOf(Order.Violation.BLANK_PATIENT),
                tilClinic to setOf(Order.Violation.BLANK_CLINIC),
                tilDate to setOf(OrderTypedInput.Violation.EMPTY_DATE),
                tilTime to setOf(OrderTypedInput.Violation.EMPTY_TIME),
                tilPrice to setOf(OrderTypedInput.Violation.INVALID_PRICE)
            )
        }
    }

    override val inputErrorToText by lazy {
        val requiredText = getString(R.string.required)
        mapOf<Any, String>(
            Order.Violation.BLANK_PATIENT to requiredText,
            Order.Violation.BLANK_CLINIC to requiredText,
            OrderTypedInput.Violation.EMPTY_DATE to requiredText,
            OrderTypedInput.Violation.EMPTY_TIME to requiredText,
            OrderTypedInput.Violation.INVALID_PRICE to
                    getString(R.string.insufficient_price)
        )
    }

    private fun setupUi() = binding.run {
        etDate.setOnClickListener {
            showFulfilmentDatePicker()
        }
        etTime.setOnClickListener {
            showFulfilmentTimePicker()
        }
        etClinic.run {
            setAdapter(clinicHintAdapter)
            setOnItemClickListener { _, _, _, _ ->
                activity?.hideSoftKeyboard()
            }
        }
        etDoctor.run {
            setAdapter(doctorHintAdapter)
            setOnItemClickListener { _, _, _, _ ->
                activity?.hideSoftKeyboard()
            }
        }
    }

    private fun handleState(state: OrderState) {
        requireActivity().invalidateMenu()
        clinicHintAdapter.submitItems(state.clinicHints)
        doctorHintAdapter.submitItems(state.doctorHints)
        showInputErrors(state.violations)
    }

    private fun handleEvent(event: OrderEvent) {
        when (event) {
            is OrderEvent.ShowInput -> {
                showOrderInput(event.input)
            }
            is OrderEvent.NavigateToFormulaScreen -> {
                OrderFragmentDirections
                    .actionOrderFragmentToFormulaFragment(event.orderId)
                    .let { findNavController().navigate(it) }
            }
            is OrderEvent.ShowSavedMessage -> {
                Toast.makeText(
                    requireContext(),
                    R.string.order_saved,
                    Toast.LENGTH_SHORT
                ).show()
            }
            is OrderEvent.ShowDeletedMessage -> {
                Toast.makeText(
                    requireContext(),
                    R.string.order_deleted,
                    Toast.LENGTH_SHORT
                ).show()
            }
            OrderEvent.NavigateBack -> {
                findNavController().navigateUp()
            }
        }
    }

    private fun showOrderInput(input: OrderInput) = binding.run {
        etPatient.setText(input.patient)
        etDate.setText(
            input.date?.let { date ->
                dateFormat.format(date.toJavaDate())
            } ?: ""
        )
        etTime.setText(
            input.time?.let { time ->
                timeFormat.format(time.toJavaDate())
            } ?: ""
        )
        etClinic.setText(input.customerClinic)
        etDoctor.setText(input.customerDoctor)
        etPrice.setText(input.paymentPrice)
        cbPaid.isChecked = input.paymentIsDone
        etNote.setText(input.note)
    }

    private fun showFulfilmentDatePicker() {
        MaterialDatePicker.Builder.datePicker().build()
            .apply {
                addOnPositiveButtonClickListener { millis: Long ->
                    val dateFormatted = dateFormat.format(Date(millis))
                    binding.etDate.setText(dateFormatted)
                }
            }
            .show(childFragmentManager, null)
    }

    private fun showFulfilmentTimePicker() {
        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    val millis = LocalTime(hour, minute)
                        .toMillisecondOfDay().toLong()
                    val timeFormatted = timeFormat.format(Date(millis))
                    binding.etTime.setText(timeFormatted)
                }
            }
            .show(childFragmentManager, null)
    }

    private fun sendOrderInput() = binding.run {
        val input = OrderInput(
            patient = etPatient.text?.toString() ?: "",
            date = etDate.text?.let { text ->
                dateFormat.parseOrNull(text.toString())
                    ?.toLocalDateTime(TimeZone.UTC)
                    ?.date
            },
            time = etTime.text?.let { text ->
                timeFormat.parseOrNull(text.toString())
                    ?.toLocalDateTime(TimeZone.UTC)
                    ?.time
            },
            customerClinic = etClinic.text?.toString() ?: "",
            customerDoctor = etDoctor.text?.toString() ?: "",
            paymentPrice = etPrice.text?.toString() ?: "",
            paymentIsDone = cbPaid.isChecked,
            note = etNote.text?.toString() ?: ""
        )
        viewModel.intents.trySend(OrderIntent.Save(input))
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.order, menu)
    }

    override fun onPrepareMenu(menu: Menu) = menu.run {
        val state = viewModel.state.value
        findItem(R.id.mi_formula).isVisible =
            state.mode is OrderState.Mode.Editing
        findItem(R.id.mi_delete).isVisible =
            state.mode is OrderState.Mode.Editing
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.mi_save -> sendOrderInput()
            R.id.mi_formula ->
                viewModel.intents.trySend(OrderIntent.ShowFormula)
            R.id.mi_delete ->
                viewModel.intents.trySend(OrderIntent.Delete)
            else -> return false
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderBinding.bind(view)
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
}