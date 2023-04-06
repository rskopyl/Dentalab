package com.rskopyl.dentalab.ui.settings.component.material

import androidx.lifecycle.viewModelScope
import com.rskopyl.dentalab.data.model.Material
import com.rskopyl.dentalab.repository.MaterialRepository
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.ValidationException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaterialViewModel @AssistedInject constructor(
    @Assisted mode: MaterialState.Mode,
    private val materialRepository: MaterialRepository
) : IntentViewModel<MaterialState, MaterialEvent, MaterialIntent>(
    initialState = MaterialState(mode)
) {
    init {
        intents.trySend(MaterialIntent.Load(mode))
    }

    override suspend fun handleIntent(intent: MaterialIntent) {
        when (intent) {
            is MaterialIntent.Load -> load(intent.mode)
            is MaterialIntent.Save -> save(intent.input)
            MaterialIntent.Delete -> delete()
            MaterialIntent.Cancel ->
                _events.trySend(MaterialEvent.NavigateBack)
        }
    }

    private fun load(mode: MaterialState.Mode) {
        if (mode !is MaterialState.Mode.Editing) return
        viewModelScope.launch(Dispatchers.IO) {
            val input = materialRepository
                .getById(mode.materialId)
                .firstOrNull()
                ?.let(::MaterialTypedInput) ?: return@launch
            _events.trySend(MaterialEvent.ShowInput(input))
        }
    }

    private fun save(input: MaterialTypedInput) {
        val material = try {
            Material(
                id = when (val mode = _state.value.mode) {
                    MaterialState.Mode.Creating -> Material.EMPTY_ID
                    is MaterialState.Mode.Editing -> mode.materialId
                },
                name = input.name.trim(),
                color = input.color
            )
        } catch (e: ValidationException) {
            _state.update { state ->
                state.copy(violations = setOf(e.violation))
            }
            return
        }
        when (_state.value.mode) {
            MaterialState.Mode.Creating ->
                materialRepository.insert(material)
            is MaterialState.Mode.Editing ->
                materialRepository.update(material)
        }
        _events.trySend(MaterialEvent.NavigateBack)
    }

    private fun delete() {
        _state.value.mode.let { mode ->
            if (mode !is MaterialState.Mode.Editing) return
            materialRepository.deleteById(mode.materialId)
        }
        _events.trySend(MaterialEvent.NavigateBack)
    }

    @AssistedFactory
    interface DaggerFactory {

        fun create(mode: MaterialState.Mode): MaterialViewModel
    }
}