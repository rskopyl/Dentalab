package com.rskopyl.dentalab.ui.formula.modification

import com.rskopyl.dentalab.data.model.Components
import com.rskopyl.dentalab.data.model.Modification

data class ModificationState(
    val mode: Mode,
    val modification: Pair<Modification, Components> =
        Modification(mode.orderId, mode.position) to Components(),
    val structureNames: List<String> = emptyList(),
    val materialNames: List<String> = emptyList(),
    val violations: Set<Any> = emptySet()
) {
    sealed class Mode(val orderId: Long, val position: Int) {

        class Creating(orderId: Long, position: Int) : Mode(orderId, position)
        class Editing(orderId: Long, position: Int) : Mode(orderId, position)
    }
}

sealed interface ModificationEvent {

    data class ShowInput(val input: ModificationInput) : ModificationEvent
    object NavigateBack : ModificationEvent
}

sealed interface ModificationIntent {

    data class Load(val mode: ModificationState.Mode) : ModificationIntent
    data class Apply(val input: ModificationInput) : ModificationIntent
    data class Save(val input: ModificationInput) : ModificationIntent
    object Delete : ModificationIntent
    object Cancel : ModificationIntent
}