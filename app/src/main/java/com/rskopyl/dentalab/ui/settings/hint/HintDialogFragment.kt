package com.rskopyl.dentalab.ui.settings.hint

import android.app.Dialog
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.data.model.Hint
import com.rskopyl.dentalab.databinding.DialogHintBinding
import com.rskopyl.dentalab.util.InputErrorsMedia
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.collectOnLifecycle
import com.rskopyl.dentalab.util.invoke
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HintDialogFragment :
    DialogFragment(R.layout.dialog_hint),
    InputErrorsMedia {

    @Inject
    lateinit var viewModelDaggerFactory: HintViewModel.DaggerFactory

    private val args by navArgs<HintDialogFragmentArgs>()

    private val viewModel:
            IntentViewModel<HintState, HintEvent, HintIntent>
            by viewModels<HintViewModel> {
                ViewModelProvider.Factory {
                    viewModelDaggerFactory.create(
                        mode = args.hintText.let { text ->
                            if (text == null) {
                                HintState.Mode.Creating
                            } else {
                                HintState.Mode.Editing(text)
                            }
                        },
                        target = args.hintTarget
                    )
                }
            }

    private var _binding: DialogHintBinding? = null
    private val binding get() = checkNotNull(_binding)

    override val inputFieldToErrors by lazy {
        binding.run {
            mapOf<TextInputLayout, Set<Any>>(
                tilHint to setOf(Hint.Violation.BLANK_TEXT)
            )
        }
    }

    override val inputErrorToText by lazy {
        mapOf<Any, String>(
            Hint.Violation.BLANK_TEXT to getString(R.string.required)
        )
    }

    private fun setupUi() = binding.run {
        layoutDialogActions.run {
            btnSave.setOnClickListener {
                sendHintInput()
            }
            btnDelete.setOnClickListener {
                viewModel.intents.trySend(HintIntent.Delete)
            }
            btnCancel.setOnClickListener {
                viewModel.intents.trySend(HintIntent.Cancel)
            }
        }
    }

    private fun handleState(state: HintState) {
        showInputErrors(state.violations)
        binding.layoutDialogActions.btnDelete.isVisible =
            state.mode is HintState.Mode.Editing
    }

    private fun handleEvent(event: HintEvent) {
        when (event) {
            is HintEvent.ShowInput -> showHintInput(event.input)
            HintEvent.NavigateBack -> dismiss()
        }
    }

    private fun sendHintInput() {
        val input = HintTypedInput(
            text = binding.etHint.text?.toString() ?: ""
        )
        viewModel.intents.trySend(HintIntent.Save(input))
    }

    private fun showHintInput(
        input: HintTypedInput
    ) = binding.run {
        etHint.setText(input.text)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogHintBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(
                when(args.hintTarget) {
                    Hint.Target.CLINIC -> R.string.clinic
                    Hint.Target.DOCTOR -> R.string.doctor
                }
            )
            .create()
    }

    override fun onStart() {
        super.onStart()
        setupUi()
        viewModel.run {
            state.collectOnLifecycle(
                viewLifecycleOwner,
                collector = ::handleState
            )
            events.collectOnLifecycle(
                viewLifecycleOwner,
                collector = ::handleEvent
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}