package com.rskopyl.dentalab.ui.settings

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.data.model.Hint
import com.rskopyl.dentalab.data.preferences.AppPreferences
import com.rskopyl.dentalab.databinding.FragmentSettingsBinding
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel:
            IntentViewModel<SettingsState, SettingsEvent, SettingsIntent>
            by viewModels<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val homePeriodToText by lazy {
        mapOf<AppPreferences.HomePeriod, String>(
            AppPreferences.HomePeriod.WEEK to getString(R.string.week),
            AppPreferences.HomePeriod.DEKADE to getString(R.string.dekade),
            AppPreferences.HomePeriod.FORTNIGHT to getString(R.string.fortnight),
            AppPreferences.HomePeriod.MONTH to getString(R.string.month)
        )
    }

    private fun setupUi() = binding.run {
        btnHomePeriod.setOnClickListener {
            showHomePeriodDialog()
        }
        switchQuickNavigation.setOnClickListener {
            viewModel.intents.trySend(
                SettingsIntent.ChangeQuickNavigation(
                    isEnabled = switchQuickNavigation.isChecked
                )
            )
        }
        btnStructures.setOnClickListener {
            val state = viewModel.state.value
            showItemsWithCreateOptionDialog(
                titleId = R.string.structures,
                items = state.structureNames,
                onCreateItem = {
                    viewModel.intents.trySend(SettingsIntent.CreateStructure)
                },
                onEditItem = { index ->
                    viewModel.intents.trySend(
                        SettingsIntent.ShowStructureDetails(
                            structureName = state.structureNames[index]
                        )
                    )
                }
            )
        }
        btnMaterials.setOnClickListener {
            val state = viewModel.state.value
            showItemsWithCreateOptionDialog(
                titleId = R.string.materials,
                items = state.materialNames,
                onCreateItem = {
                    viewModel.intents.trySend(SettingsIntent.CreateMaterial)
                },
                onEditItem = { index ->
                    viewModel.intents.trySend(
                        SettingsIntent.ShowMaterialDetails(
                            materialName = state.materialNames[index]
                        )
                    )
                }
            )
        }
        btnClinics.setOnClickListener {
            val state = viewModel.state.value
            showItemsWithCreateOptionDialog(
                titleId = R.string.clinics,
                items = state.clinicHints,
                onCreateItem = {
                    viewModel.intents.trySend(
                        SettingsIntent.CreateHint(Hint.Target.CLINIC)
                    )
                },
                onEditItem = { index ->
                    viewModel.intents.trySend(
                        SettingsIntent.ShowHintDetails(
                            hintTarget = Hint.Target.CLINIC,
                            hintText = state.clinicHints[index]
                        )
                    )
                }
            )
        }
        btnDoctors.setOnClickListener {
            val state = viewModel.state.value
            showItemsWithCreateOptionDialog(
                titleId = R.string.doctors,
                items = state.doctorHints,
                onCreateItem = {
                    viewModel.intents.trySend(
                        SettingsIntent.CreateHint(Hint.Target.DOCTOR)
                    )
                },
                onEditItem = { index ->
                    viewModel.intents.trySend(
                        SettingsIntent.ShowHintDetails(
                            hintTarget = Hint.Target.DOCTOR,
                            hintText = state.doctorHints[index]
                        )
                    )
                }
            )
        }
    }

    private fun handleState(state: SettingsState) = binding.run {
        tvHomePeriod.isVisible = state.preferences != null
        switchQuickNavigation.isVisible = state.preferences != null
        state.preferences?.run {
            tvHomePeriod.text = homePeriodToText[homePeriod]
            switchQuickNavigation.isChecked = isQuickNavigationEnabled
        }
    }

    private fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.NavigateToStructureScreen -> {
                SettingsFragmentDirections
                    .actionSettingsFragmentToStructureDialogFragment(
                        event.structureId
                    )
                    .let { findNavController().navigate(it) }
            }
            is SettingsEvent.NavigateToMaterialScreen -> {
                SettingsFragmentDirections
                    .actionSettingsFragmentToMaterialDialogFragment(
                        event.materialId
                    )
                    .let { findNavController().navigate(it) }
            }
            is SettingsEvent.NavigateToHintScreen -> {
                SettingsFragmentDirections
                    .actionSettingsFragmentToHintDialogFragment(
                        event.target, event.text
                    )
                    .let { findNavController().navigate(it) }
            }
        }
    }

    private fun showHomePeriodDialog() {
        val homePeriodTexts = homePeriodToText.values.toTypedArray()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.home_period)
            .setItems(homePeriodTexts) { _, index ->
                val homePeriod = homePeriodToText.keys.elementAt(index)
                viewModel.intents.trySend(
                    SettingsIntent.ChangeHomePeriod(homePeriod)
                )
            }
            .show()
    }

    private fun showItemsWithCreateOptionDialog(
        @StringRes titleId: Int,
        items: Collection<String>,
        onCreateItem: (() -> Unit),
        onEditItem: ((Int) -> Unit),
    ) {
        val createOptionName = getString(R.string.add_new)
        val options = items.toMutableList()
            .apply { add(0, createOptionName) }
            .toList()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(titleId)
            .setItems(options.toTypedArray()) { _, index ->
                if (index == 0) {
                    onCreateItem()
                } else {
                    onEditItem(index.dec())
                }
            }
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
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