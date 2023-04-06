package com.rskopyl.dentalab.ui.order

import androidx.lifecycle.viewModelScope
import com.rskopyl.dentalab.data.model.Hint
import com.rskopyl.dentalab.data.model.Order
import com.rskopyl.dentalab.data.preferences.AppPreferencesManager
import com.rskopyl.dentalab.repository.HintRepository
import com.rskopyl.dentalab.repository.OrderRepository
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.ValidationException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class OrderViewModel @AssistedInject constructor(
    @Assisted mode: OrderState.Mode,
    appPreferencesManager: AppPreferencesManager,
    private val orderRepository: OrderRepository,
    private val hintRepository: HintRepository
) : IntentViewModel<OrderState, OrderEvent, OrderIntent>(
    initialState = OrderState(mode = mode)
) {
    private val isQuickNavigationEnabled = appPreferencesManager.get()
        .map { it.isQuickNavigationEnabled }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        intents.trySend(OrderIntent.Load(mode))
    }

    override suspend fun handleIntent(intent: OrderIntent) {
        when (intent) {
            is OrderIntent.Load -> load(intent.mode)
            is OrderIntent.Save -> save(intent.input)
            is OrderIntent.ShowFormula -> showOrderFormula()
            is OrderIntent.Delete -> deleteOrder()
        }
    }

    private fun load(mode: OrderState.Mode) {
        viewModelScope.launch {
            val input = when (mode) {
                is OrderState.Mode.Creating -> {
                    OrderInput(
                        date = LocalDate.fromEpochDays(mode.epochDays)
                    )
                }
                is OrderState.Mode.Editing -> {
                    OrderInput(
                        order = orderRepository
                            .getById(mode.orderId)
                            .first()
                            .let(::requireNotNull)
                    )
                }
            }
            _events.trySend(OrderEvent.ShowInput(input))
            combine(
                hintRepository.getByTarget(Hint.Target.CLINIC),
                hintRepository.getByTarget(Hint.Target.DOCTOR)
            ) { clinicHints, doctorHints ->
                _state.update { state ->
                    state.copy(
                        clinicHints = clinicHints.map { it.text },
                        doctorHints = doctorHints.map { it.text }
                    )
                }
            }.collect()
        }
    }

    private fun save(input: OrderInput) {
        val order = try {
            val typedInput = OrderTypedInput(input)
            Order(
                id = when (val mode = _state.value.mode) {
                    is OrderState.Mode.Creating -> Order.EMPTY_ID
                    is OrderState.Mode.Editing -> mode.orderId
                },
                patient = typedInput.patient.trim(),
                customer = Order.Customer(
                    clinic = typedInput.customerClinic.trim(),
                    doctor = typedInput.customerDoctor.trim()
                ),
                dateTime = typedInput.dateTime,
                payment = Order.Payment(
                    price = typedInput.paymentPrice,
                    isDone = typedInput.paymentIsDone
                ),
                note = typedInput.note.trim()
            )
        } catch (e: ValidationException) {
            _state.update { state ->
                state.copy(violations = setOf(e.violation))
            }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val orderId = when (val mode = _state.value.mode) {
                is OrderState.Mode.Creating -> {
                    orderRepository.insert(order).await().also { orderId ->
                        if (isQuickNavigationEnabled.value) {
                            _events.trySend(
                                OrderEvent.NavigateToFormulaScreen(orderId)
                            )
                        }
                    }
                }
                is OrderState.Mode.Editing -> {
                    orderRepository.update(order)
                    mode.orderId
                }
            }
            _events.trySend(OrderEvent.ShowSavedMessage)
            handleIntent(
                OrderIntent.Load(
                    OrderState.Mode.Editing(orderId)
                )
            )
        }
    }

    private fun showOrderFormula() {
        when(val mode = _state.value.mode) {
            is OrderState.Mode.Editing -> {
                _events.trySend(
                    OrderEvent.NavigateToFormulaScreen(
                        orderId = mode.orderId
                    )
                )
            }
            else -> return
        }
    }

    private fun deleteOrder() {
        when (val mode = _state.value.mode) {
            is OrderState.Mode.Editing -> {
                orderRepository.deleteById(mode.orderId)
                _events.trySend(OrderEvent.ShowDeletedMessage)
                _events.trySend(OrderEvent.NavigateBack)
            }
            else -> return
        }
    }

    @AssistedFactory
    interface DaggerFactory {

        fun create(mode: OrderState.Mode): OrderViewModel
    }
}