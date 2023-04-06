package com.rskopyl.dentalab.ui.settings.component.material

data class MaterialState(
    val mode: Mode,
    val violations: Set<Any> = emptySet()
) {
    sealed interface Mode {

        object Creating : Mode
        data class Editing(val materialId: Long) : Mode
    }
}

sealed interface MaterialEvent {

    data class ShowInput(val input: MaterialTypedInput) : MaterialEvent
    object NavigateBack : MaterialEvent
}

sealed interface MaterialIntent {

    data class Load(val mode: MaterialState.Mode) : MaterialIntent
    data class Save(val input: MaterialTypedInput) : MaterialIntent
    object Delete : MaterialIntent
    object Cancel : MaterialIntent
}