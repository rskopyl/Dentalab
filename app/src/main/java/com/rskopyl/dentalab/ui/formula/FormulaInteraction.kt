package com.rskopyl.dentalab.ui.formula

import com.rskopyl.dentalab.data.model.Components
import com.rskopyl.dentalab.data.model.Material
import com.rskopyl.dentalab.data.model.Modification

data class FormulaState(
    val orderId: Long,
    val formula: Map<Modification, Components> = emptyMap(),
    val materials: List<Material> = emptyList(),
    val copiedModification: Modification? = null,
    val isErasingEnabled: Boolean = false
) {
    val areToolsClear: Boolean
        get() = copiedModification == null && !isErasingEnabled
}

sealed interface FormulaEvent {

    data class NavigateToModificationScreen(
        val orderId: Long, val position: Int
    ) : FormulaEvent

    object ShowSavedMessage : FormulaEvent
    object ShowHasUnlinkedComponentsMessage : FormulaEvent
    object NavigateBack : FormulaEvent
}

sealed interface FormulaIntent {

    data class Load(val orderId: Long) : FormulaIntent
    data class ShowModificationDetails(val position: Int) : FormulaIntent
    data class CopyModification(val position: Int) : FormulaIntent
    data class PasteModification(val position: Int) : FormulaIntent
    data class EraseModification(val position: Int) : FormulaIntent
    object EnableErasingMode : FormulaIntent
    object ClearTools : FormulaIntent
    object Delete : FormulaIntent
}