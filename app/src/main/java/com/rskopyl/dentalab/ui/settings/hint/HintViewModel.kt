package com.rskopyl.dentalab.ui.settings.hint

import com.rskopyl.dentalab.data.model.Hint
import com.rskopyl.dentalab.repository.HintRepository
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.ValidationException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.update

class HintViewModel @AssistedInject constructor(
    @Assisted mode: HintState.Mode,
    @Assisted target: Hint.Target,
    private val hintRepository: HintRepository
) : IntentViewModel<HintState, HintEvent, HintIntent>(
    initialState = HintState(mode, target)
) {
    init {
        intents.trySend(HintIntent.Load(mode))
    }

    override suspend fun handleIntent(intent: HintIntent) {
        when (intent) {
            is HintIntent.Load -> load(intent.mode)
            is HintIntent.Save -> save(intent.input)
            HintIntent.Delete -> delete()
            HintIntent.Cancel ->
                _events.trySend(HintEvent.NavigateBack)
        }
    }

    private fun load(mode: HintState.Mode) {
        if (mode is HintState.Mode.Editing) {
            _events.trySend(
                HintEvent.ShowInput(
                    HintTypedInput(mode.text)
                )
            )
        }
    }

    private fun save(input: HintTypedInput) {
        val hint = try {
            Hint(
                text = input.text.trim(),
                target = _state.value.target
            )
        } catch (e: ValidationException) {
            _state.update { state ->
                state.copy(violations = setOf(e.violation))
            }
            return
        }
        when (_state.value.mode) {
            HintState.Mode.Creating ->
                hintRepository.insert(hint)
            is HintState.Mode.Editing ->
                hintRepository.update(hint)
        }
        _events.trySend(HintEvent.NavigateBack)
    }

    private fun delete() {
        _state.value.let { state ->
            if (state.mode !is HintState.Mode.Editing) return
            hintRepository.delete(
                Hint(
                    text = state.mode.text,
                    target = state.target
                )
            )
        }
        _events.trySend(HintEvent.NavigateBack)
    }

    @AssistedFactory
    interface DaggerFactory {

        fun create(mode: HintState.Mode, target: Hint.Target): HintViewModel
    }
}