package com.rskopyl.dentalab.ui.formula

import androidx.lifecycle.viewModelScope
import com.rskopyl.dentalab.data.model.Components
import com.rskopyl.dentalab.data.model.Modification
import com.rskopyl.dentalab.repository.ModificationRepository
import com.rskopyl.dentalab.util.IntentViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FormulaViewModel @AssistedInject constructor(
    @Assisted orderId: Long,
    private val modificationRepository: ModificationRepository
) : IntentViewModel<FormulaState, FormulaEvent, FormulaIntent>(
    initialState = FormulaState(orderId)
) {
    init {
        intents.trySend(FormulaIntent.Load(orderId))
    }

    override suspend fun handleIntent(intent: FormulaIntent) {
        when (intent) {
            is FormulaIntent.Load ->
                load(intent.orderId)
            is FormulaIntent.ShowModificationDetails ->
                showModificationDetails(intent.position)
            is FormulaIntent.CopyModification ->
                copyModification(intent.position)
            is FormulaIntent.PasteModification ->
                pasteModification(intent.position)
            is FormulaIntent.EraseModification ->
                eraseModification(intent.position)
            FormulaIntent.EnableErasingMode -> enableErasingMode()
            FormulaIntent.ClearTools -> clearTools()
            FormulaIntent.Delete -> delete()
        }
    }

    private fun showModificationDetails(position: Int) {
        _events.trySend(
            FormulaEvent.NavigateToModificationScreen(
                orderId = _state.value.orderId, position = position
            )
        )
    }

    private fun copyModification(position: Int) {
        val modification = _state.value.formula.keys
            .singleOrNull { it.position == position }
        _state.update { state ->
            state.copy(copiedModification = modification)
        }
    }

    private fun pasteModification(position: Int) {
        _state.value.copiedModification?.let { origin ->
            modificationRepository.insert(
                origin.copy(position = position)
            )
        }
    }

    private fun eraseModification(position: Int) {
        modificationRepository.deleteByOrderIdAndPosition(
            orderId = _state.value.orderId, position = position
        )
    }

    private fun load(orderId: Long) {
        viewModelScope.launch {
            modificationRepository
                .getByOrderId(orderId)
                .collectLatest { formula ->
                    _state.update { state ->
                        state.copy(
                            formula = formula.complementedWithEmpty(orderId),
                            materials = formula
                                .mapNotNull { (_, components) ->
                                    components.material
                                }
                                .distinct()
                                .sortedBy { material -> material.name }
                        )
                    }
                }
        }
    }

    private fun enableErasingMode() {
        _state.update { state ->
            state.copy(isErasingEnabled = true)
        }
    }

    private fun clearTools() {
        _state.update { state ->
            state.copy(
                copiedModification = null,
                isErasingEnabled = false
            )
        }
    }

    private fun delete() {
        modificationRepository.deleteByOrderId(_state.value.orderId)
    }

    @AssistedFactory
    interface DaggerFactory {

        fun create(orderId: Long): FormulaViewModel
    }
}

private fun Map<Modification, Components>.complementedWithEmpty(
    orderId: Long
): Map<Modification, Components> {
    val formula = mutableMapOf<Modification, Components>()
    for (position in Modification.POSITIONS) {
        val (modification, components) = entries
            .firstOrNull { (modification, _) ->
                modification.position == position
            }
            ?.toPair()
            ?: (Modification(orderId, position) to Components())
        formula[modification] = components
    }
    return formula
}