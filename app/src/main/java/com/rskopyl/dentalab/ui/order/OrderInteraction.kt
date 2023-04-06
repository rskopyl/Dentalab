package com.rskopyl.dentalab.ui.order

data class OrderState(
    val mode: Mode,
    val clinicHints: List<String> = emptyList(),
    val doctorHints: List<String> = emptyList(),
    val violations: Set<Any> = emptySet()
) {
    sealed interface Mode {

        data class Creating(val epochDays: Int) : Mode
        data class Editing(val orderId: Long) : Mode
    }
}

sealed interface OrderEvent {

    data class ShowInput(val input: OrderInput) : OrderEvent
    data class NavigateToFormulaScreen(val orderId: Long) : OrderEvent
    object ShowSavedMessage : OrderEvent
    object ShowDeletedMessage : OrderEvent
    object NavigateBack : OrderEvent
}

sealed interface OrderIntent {

    data class Load(val mode: OrderState.Mode) : OrderIntent
    data class Save(val input: OrderInput) : OrderIntent
    object ShowFormula : OrderIntent
    object Delete : OrderIntent
}