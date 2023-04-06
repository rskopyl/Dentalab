package com.rskopyl.dentalab.ui.formula

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.data.model.Modification
import com.rskopyl.dentalab.databinding.FragmentFormulaBinding
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.SpacingItemDecoration
import com.rskopyl.dentalab.util.collectOnLifecycle
import com.rskopyl.dentalab.util.invoke
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FormulaFragment : Fragment(R.layout.fragment_formula),
    MenuProvider {

    @Inject
    lateinit var viewModelDaggerFactory: FormulaViewModel.DaggerFactory

    private val viewModel:
            IntentViewModel<FormulaState, FormulaEvent, FormulaIntent>
            by navGraphViewModels<FormulaViewModel>(
                navGraphId = R.id.formulaFragment
            ) {
                val args by navArgs<FormulaFragmentArgs>()
                ViewModelProvider.Factory {
                    viewModelDaggerFactory.create(args.orderId)
                }
            }

    private var _binding: FragmentFormulaBinding? = null
    private val binding get() = checkNotNull(_binding)

    private var actionMode: ActionMode? = null

    private val modificationAdapter by lazy {
        ModificationAdapter(
            context = requireContext(),
            onItemClick = { (it: Modification, _) ->
                val state = viewModel.state.value
                viewModel.intents.trySend(
                    if (state.copiedModification != null) {
                        FormulaIntent.PasteModification(it.position)
                    } else if (state.isErasingEnabled) {
                        FormulaIntent.EraseModification(it.position)
                    } else {
                        FormulaIntent.ShowModificationDetails(it.position)
                    }
                )
            },
            onItemLongClick = { (it, _) ->
                if (!viewModel.state.value.isErasingEnabled) {
                    viewModel.intents.trySend(
                        FormulaIntent.CopyModification(it.position)
                    )
                }
            }
        )
    }
    private val materialAdapter = MaterialAdapter()

    private fun setupUi() = binding.run {
        rvModifications.adapter = modificationAdapter
        rvMaterials.run {
            adapter = materialAdapter
            layoutManager = if (
                resources.configuration.orientation ==
                Configuration.ORIENTATION_PORTRAIT
            ) {
                LinearLayoutManager(requireContext())
            } else {
                GridLayoutManager(
                    requireContext(),
                    LANDSCAPE_MATERIAL_LEGEND_COLUMNS
                )
            }
            addItemDecoration(
                SpacingItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.small_50)
                )
            )
        }
    }

    private fun handleState(state: FormulaState) {
        modificationAdapter.submitList(
            state.formula.toList().sortedBy { (modification, _) ->
                modification.position
            }
        )
        materialAdapter.submitList(state.materials)
        if (state.areToolsClear) {
            actionMode = null
        } else if (actionMode == null) {
            if (state.copiedModification != null) {
                actionMode = activity?.startActionMode(
                    ToolsMenuActionModeCallback(titleId = R.string.copying)
                )
            } else if (state.isErasingEnabled) {
                actionMode = activity?.startActionMode(
                    ToolsMenuActionModeCallback(titleId = R.string.eraser)
                )
            }
        }
    }

    private fun handleEvent(event: FormulaEvent) {
        when (event) {
            is FormulaEvent.NavigateToModificationScreen -> {
                FormulaFragmentDirections
                    .actionFormulaFragmentToModificationDialogFragment(
                        orderId = event.orderId, position = event.position
                    )
                    .let { findNavController().navigate(it) }
            }
            FormulaEvent.ShowSavedMessage -> {
                Toast.makeText(
                    requireContext(),
                    R.string.formula_saved,
                    Toast.LENGTH_SHORT
                ).show()
            }
            FormulaEvent.ShowHasUnlinkedComponentsMessage -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.linking_error)
                    .setMessage(R.string.linking_error_message)
                    .setPositiveButton(R.string.ok, null)
                    .show()
            }
            FormulaEvent.NavigateBack -> {
                findNavController().navigateUp()
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.formula, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.mi_tools) {
            PopupMenu(
                requireContext(),
                requireActivity().findViewById(R.id.mi_tools)
            ).apply {
                inflate(R.menu.formula_tools)
                setForceShowIcon(true)
                setOnMenuItemClickListener(::onMenuItemSelected)
            }.show()
            return true
        }
        viewModel.intents.trySend(
            when (menuItem.itemId) {
                R.id.mi_delete -> FormulaIntent.Delete
                R.id.mi_eraser -> FormulaIntent.EnableErasingMode
                else -> return false
            }
        )
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFormulaBinding.bind(view)
        setupUi()
        viewModel.run {
            state.collectOnLifecycle(
                viewLifecycleOwner,
                collector = ::handleState
            )
            events.collectOnLifecycle(
                viewLifecycleOwner,
                state = Lifecycle.State.RESUMED,
                collector = ::handleEvent
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {

        const val LANDSCAPE_MATERIAL_LEGEND_COLUMNS = 3
    }

    inner class ToolsMenuActionModeCallback(
        @StringRes private val titleId: Int
    ) : ActionMode.Callback {

        override fun onCreateActionMode(
            actionMode: ActionMode?,
            menu: Menu?
        ): Boolean {
            actionMode?.setTitle(titleId)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) =
            false

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) =
            false

        override fun onDestroyActionMode(actionMode: ActionMode?) {
            viewModel.intents.trySend(FormulaIntent.ClearTools)
        }
    }
}