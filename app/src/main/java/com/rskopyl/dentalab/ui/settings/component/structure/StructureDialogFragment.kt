package com.rskopyl.dentalab.ui.settings.component.structure

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
import com.rskopyl.dentalab.data.model.Structure
import com.rskopyl.dentalab.databinding.DialogStructureBinding
import com.rskopyl.dentalab.util.InputErrorsMedia
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.collectOnLifecycle
import com.rskopyl.dentalab.util.invoke
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StructureDialogFragment :
    DialogFragment(R.layout.dialog_structure),
    InputErrorsMedia {

    @Inject
    lateinit var viewModelDaggerFactory: StructureViewModel.DaggerFactory

    private val viewModel:
            IntentViewModel<StructureState, StructureEvent, StructureIntent>
            by viewModels {
                val args = navArgs<StructureDialogFragmentArgs>().value
                ViewModelProvider.Factory {
                    viewModelDaggerFactory.create(
                        mode = if (args.structureId == Structure.EMPTY_ID) {
                            StructureState.Mode.Creating
                        } else {
                            StructureState.Mode.Editing(args.structureId)
                        }
                    )
                }
            }

    private var _binding: DialogStructureBinding? = null
    private val binding get() = checkNotNull(_binding)

    override val inputFieldToErrors by lazy {
        binding.run {
            mapOf<TextInputLayout, Set<Any>>(
                tilName to setOf(Structure.Violation.BLANK_NAME),
                tilSymbol to setOf(Structure.Violation.INVALID_SYMBOL)
            )
        }
    }
    override val inputErrorToText by lazy {
        mapOf<Any, String>(
            Structure.Violation.BLANK_NAME to
                    getString(R.string.required),
            Structure.Violation.INVALID_SYMBOL to
                    getString(R.string.invalid_symbol)
        )
    }

    private fun setupUi() = binding.layoutDialogActions.run {
        btnSave.setOnClickListener {
            sendStructureInput()
        }
        btnDelete.setOnClickListener {
            viewModel.intents.trySend(StructureIntent.Delete)
        }
        btnCancel.setOnClickListener {
            viewModel.intents.trySend(StructureIntent.Cancel)
        }
    }

    private fun handleState(state: StructureState) {
        showInputErrors(state.violations)
        binding.layoutDialogActions.btnDelete.isVisible =
            state.mode is StructureState.Mode.Editing
    }

    private fun handleEvent(event: StructureEvent) {
        when (event) {
            is StructureEvent.ShowInput -> showStructureInput(event.input)
            StructureEvent.NavigateBack -> dismiss()
        }
    }

    private fun showStructureInput(
        input: StructureTypedInput
    ) = binding.run {
        etName.setText(input.name)
        etSymbol.setText(input.symbol)
    }

    private fun sendStructureInput() = binding.run {
        val input = StructureTypedInput(
            name = etName.text?.toString() ?: "",
            symbol = etSymbol.text?.toString() ?: ""
        )
        viewModel.intents.trySend(StructureIntent.Save(input))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogStructureBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(R.string.structure)
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