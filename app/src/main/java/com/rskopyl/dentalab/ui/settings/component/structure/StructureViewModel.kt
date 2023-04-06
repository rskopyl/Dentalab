package com.rskopyl.dentalab.ui.settings.component.structure

import androidx.lifecycle.viewModelScope
import com.rskopyl.dentalab.data.model.Structure
import com.rskopyl.dentalab.repository.StructureRepository
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.ValidationException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StructureViewModel @AssistedInject constructor(
    @Assisted mode: StructureState.Mode,
    private val structureRepository: StructureRepository
) : IntentViewModel<StructureState, StructureEvent, StructureIntent>(
    initialState = StructureState(mode)
) {
    init {
        intents.trySend(StructureIntent.Load(mode))
    }

    override suspend fun handleIntent(intent: StructureIntent) {
        when (intent) {
            is StructureIntent.Load -> load(intent.mode)
            is StructureIntent.Save -> save(intent.input)
            StructureIntent.Delete -> delete()
            StructureIntent.Cancel ->
                _events.trySend(StructureEvent.NavigateBack)
        }
    }

    private fun load(mode: StructureState.Mode) {
        if (mode !is StructureState.Mode.Editing) return
        viewModelScope.launch(Dispatchers.IO) {
            val input = structureRepository
                .getById(mode.structureId)
                .firstOrNull()
                ?.let(::StructureTypedInput) ?: return@launch
            _events.trySend(StructureEvent.ShowInput(input))
        }
    }

    private fun save(input: StructureTypedInput) {
        val structure = try {
            Structure(
                id = when(val mode = _state.value.mode) {
                    StructureState.Mode.Creating -> Structure.EMPTY_ID
                    is StructureState.Mode.Editing -> mode.structureId
                },
                name = input.name.trim(),
                symbol = input.symbol
            )
        } catch (e: ValidationException) {
            _state.update { state ->
                state.copy(violations = setOf(e.violation))
            }
            return
        }
        when (_state.value.mode) {
            StructureState.Mode.Creating ->
                structureRepository.insert(structure)
            is StructureState.Mode.Editing ->
                structureRepository.update(structure)
        }
        _events.trySend(StructureEvent.NavigateBack)
    }

    private fun delete() {
        _state.value.mode.let { mode ->
            if (mode !is StructureState.Mode.Editing) return
            structureRepository.deleteById(mode.structureId)
        }
        _events.trySend(StructureEvent.NavigateBack)
    }

    @AssistedFactory
    interface DaggerFactory {

        fun create(mode: StructureState.Mode): StructureViewModel
    }
}