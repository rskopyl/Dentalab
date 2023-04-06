package com.rskopyl.dentalab.ui.settings.component.material

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.data.model.Material
import com.rskopyl.dentalab.databinding.DialogMaterialBinding
import com.rskopyl.dentalab.util.*
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorListener
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Integer.toHexString
import javax.inject.Inject

@AndroidEntryPoint
class MaterialDialogFragment :
    DialogFragment(R.layout.dialog_material),
    InputErrorsMedia {

    @Inject
    lateinit var viewModelDaggerFactory: MaterialViewModel.DaggerFactory

    private val viewModel:
            IntentViewModel<MaterialState, MaterialEvent, MaterialIntent>
            by viewModels<MaterialViewModel> {
                val args = navArgs<MaterialDialogFragmentArgs>().value
                ViewModelProvider.Factory {
                    viewModelDaggerFactory.create(
                        mode = if (args.materialId == Material.EMPTY_ID) {
                            MaterialState.Mode.Creating
                        } else {
                            MaterialState.Mode.Editing(args.materialId)
                        }
                    )
                }
            }

    private var _binding: DialogMaterialBinding? = null
    private val binding get() = checkNotNull(_binding)

    override val inputFieldToErrors by lazy {
        binding.run {
            mapOf<TextInputLayout, Set<Any>>(
                tilName to setOf(Material.Violation.BLANK_NAME),
                tilColor to setOf(Material.Violation.INVALID_COLOR)
            )
        }
    }
    override val inputErrorToText by lazy {
        mapOf<Any, String>(
            Material.Violation.BLANK_NAME to
                    getString(R.string.required),
            Material.Violation.INVALID_COLOR to
                    getString(R.string.invalid_color)
        )
    }

    private val colorOnSurfaceVariant by lazy {
        requireContext().theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurfaceVariant
        )
    }

    private fun setupUi() = binding.run {
        tilColor.setEndIconOnClickListener {
            showColorPickerDialog()
        }
        etColor.doOnTextChanged { text, _, _, _ ->
            tilColor.endIconDrawable?.setTint(
                try {
                    Color.parseColor(HEX_MARKER + text)
                } catch (_: IllegalArgumentException) {
                    colorOnSurfaceVariant
                }
            )
        }
        layoutDialogActions.run {
            btnSave.setOnClickListener {
                sendMaterialInput()
            }
            btnDelete.setOnClickListener {
                viewModel.intents.trySend(MaterialIntent.Delete)
            }
            btnCancel.setOnClickListener {
                viewModel.intents.trySend(MaterialIntent.Cancel)
            }
        }
    }

    private fun handleState(state: MaterialState) {
        showInputErrors(state.violations)
        binding.layoutDialogActions.btnDelete.isVisible =
            state.mode is MaterialState.Mode.Editing
    }

    private fun handleEvent(event: MaterialEvent) {
        when (event) {
            is MaterialEvent.ShowInput -> showMaterialInput(event.input)
            MaterialEvent.NavigateBack -> dismiss()
        }
    }

    private fun showMaterialInput(
        input: MaterialTypedInput
    ) = binding.run {
        etName.setText(input.name)
        etColor.setText(input.color.drop(1))
    }

    private fun showColorPickerDialog() {
        ColorPickerDialog.Builder(requireContext())
            .setTitle(R.string.color)
            .attachBrightnessSlideBar(true)
            .attachAlphaSlideBar(false)
            .setPositiveButton(
                R.string.select,
                ColorListener { color, _ ->
                    binding.etColor.setText(
                        toHexString(color).drop(2)
                    )
                }
            )
            .setNeutralButton(R.string.cancel, null)
            .show()
    }

    private fun sendMaterialInput() = binding.run {
        val input = MaterialTypedInput(
            name = etName.text?.toString() ?: "",
            color = HEX_MARKER + (etColor.text?.toString() ?: "")
        )
        viewModel.intents.trySend(MaterialIntent.Save(input))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogMaterialBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(R.string.material)
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

    companion object {

        private const val HEX_MARKER = "#"
    }
}