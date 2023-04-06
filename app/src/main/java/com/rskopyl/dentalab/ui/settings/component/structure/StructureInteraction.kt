package com.rskopyl.dentalab.ui.settings.component.structure

data class StructureState(
    val mode: Mode,
    val violations: Set<Any> = emptySet()
) {
    sealed interface Mode {

        object Creating : Mode
        data class Editing(val structureId: Long) : Mode
    }
}

sealed interface StructureEvent {

    data class ShowInput(val input: StructureTypedInput) : StructureEvent
    object NavigateBack : StructureEvent
}

sealed interface StructureIntent {

    data class Load(val mode: StructureState.Mode) : StructureIntent
    data class Save(val input: StructureTypedInput) : StructureIntent
    object Delete : StructureIntent
    object Cancel : StructureIntent
}