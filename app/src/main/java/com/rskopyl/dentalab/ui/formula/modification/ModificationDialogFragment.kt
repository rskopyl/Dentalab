package com.rskopyl.dentalab.ui.formula.modification

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.data.model.Components
import com.rskopyl.dentalab.data.model.Modification
import com.rskopyl.dentalab.databinding.DialogModificationBinding
import com.rskopyl.dentalab.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ModificationDialogFragment :
    DialogFragment(R.layout.dialog_modification),
    InputErrorsMedia {

    @Inject
    lateinit var viewModelDaggerFactory: ModificationViewModel.DaggerFactory

    private val viewModel:
            IntentViewModel<ModificationState, ModificationEvent, ModificationIntent>
            by viewModels {
                val args = navArgs<ModificationDialogFragmentArgs>().value
                ViewModelProvider.Factory {
                    viewModelDaggerFactory.create(
                        mode = ModificationState.Mode.Creating(
                            orderId = args.orderId, position = args.position
                        )
                    )
                }
            }

    private var _binding: DialogModificationBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val structureNameAdapter by lazy {
        ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )
    }
    private val materialNameAdapter by lazy {
        ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )
    }

    private val modificationTextColor by lazy {
        ResourcesCompat.getColor(
            requireContext().resources,
            R.color.md_theme_light_onSurface,
            requireContext().theme
        )
    }
    private val emptyModificationTextColor by lazy {
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurface
        )
    }

    override val inputFieldToErrors by lazy {
        binding.run {
            mapOf<TextInputLayout, Set<Any>>(
                tilStructure to setOf(
                    ModificationTypedInput.Violation.INVALID_STRUCTURE
                ),
                tilMaterial to setOf(
                    ModificationTypedInput.Violation.INVALID_MATERIAL
                )
            )
        }
    }
    override val inputErrorToText by lazy {
        mapOf<Any, String>(
            ModificationTypedInput.Violation.INVALID_STRUCTURE to
                    getString(R.string.insufficient_structure),
            ModificationTypedInput.Violation.INVALID_MATERIAL to
                    getString(R.string.insufficient_material)
        )
    }

    private fun setupUi() = binding.run {
        tvStructure.run {
            setAdapter(structureNameAdapter)
            doOnTextChanged { _, _, _, _ ->
                viewModel.intents.trySend(
                    ModificationIntent.Apply(createModificationInput())
                )
            }
            setOnItemClickListener { _, _, _, _ ->
                activity?.hideSoftKeyboard()
            }
        }
        tvMaterial.run {
            setAdapter(materialNameAdapter)
            doOnTextChanged { _, _, _, _ ->
                viewModel.intents.trySend(
                    ModificationIntent.Apply(createModificationInput())
                )
            }
            setOnItemClickListener { _, _, _, _ ->
                activity?.hideSoftKeyboard()
            }
        }
        layoutDialogActions.run {
            btnSave.setOnClickListener {
                viewModel.intents.trySend(
                    ModificationIntent.Save(createModificationInput())
                )
            }
            btnCancel.setOnClickListener {
                viewModel.intents.trySend(ModificationIntent.Cancel)
            }
            btnDelete.setOnClickListener {
                viewModel.intents.trySend(ModificationIntent.Delete)
            }
        }
    }

    private fun handleState(state: ModificationState) {
        showModificationWithComponents(state.modification)
        structureNameAdapter.submitItems(state.structureNames)
        materialNameAdapter.submitItems(state.materialNames)
        showInputErrors(state.violations)
        binding.layoutDialogActions.btnDelete.isVisible =
            state.mode is ModificationState.Mode.Editing
    }

    private fun handleEvent(event: ModificationEvent) {
        when (event) {
            is ModificationEvent.ShowInput ->
                showModificationInput(event.input)
            ModificationEvent.NavigateBack ->
                findNavController().navigateUp()
        }
    }

    private fun showModificationInput(
        input: ModificationInput
    ) = binding.run {
        tvStructure.setText(input.structureName, false)
        tvMaterial.setText(input.materialName, false)
    }

    private fun showModificationWithComponents(
        modificationWithComponents: Pair<Modification, Components>
    ) {
        val (modification, components) = modificationWithComponents
        val (backgroundColor, textColor) = components.material
            ?.let { Color.parseColor(it.color) to modificationTextColor }
            ?: (Color.TRANSPARENT to emptyModificationTextColor)
        binding.run {
            llModification.background.setTint(backgroundColor)
            tvPosition.run {
                text = modification.displayPosition.toString()
                setTextColor(textColor)
            }
            tvSymbol.run {
                text = components.structure?.symbol ?: ""
                setTextColor(textColor)
            }
        }
    }

    private fun createModificationInput(): ModificationInput = binding.run {
        ModificationInput(
            structureName = tvStructure.text?.toString() ?: "",
            materialName = tvMaterial.text?.toString() ?: ""
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogModificationBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
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