package com.rskopyl.dentalab.ui.formula

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rskopyl.dentalab.R
import com.rskopyl.dentalab.data.model.Components
import com.rskopyl.dentalab.data.model.Modification
import com.rskopyl.dentalab.databinding.ItemModificationBinding
import com.rskopyl.dentalab.ui.formula.ModificationAdapter.ModificationViewHolder
import com.rskopyl.dentalab.util.resolveAttribute

class ModificationAdapter(
    context: Context,
    private val onItemClick: ((Pair<Modification, Components>) -> Unit)? = null,
    private val onItemLongClick: ((Pair<Modification, Components>) -> Unit)? = null
) : ListAdapter<Pair<Modification, Components>, ModificationViewHolder>(
    ModificationUiDiffCallback
) {
    private val modificationTextColor = ResourcesCompat.getColor(
        context.resources, R.color.md_theme_light_onSurface, context.theme
    )
    private val emptyModificationTextColor = context.theme.resolveAttribute(
        com.google.android.material.R.attr.colorOnSurface
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModificationViewHolder {
        return ModificationViewHolder(
            ItemModificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ModificationViewHolder,
        position: Int
    ) {
        val modification = getItem(position)
        holder.bind(modification)
    }

    inner class ModificationViewHolder(
        private val binding: ItemModificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(modificationWithComponents: Pair<Modification, Components>) {
            val (modification, components) = modificationWithComponents
            val (backgroundColor, textColor) = if (components.material != null) {
                Color.parseColor(components.material.color) to modificationTextColor
            } else {
                Color.TRANSPARENT to emptyModificationTextColor
            }
            binding.run {
                tvPosition.run {
                    text = modification.displayPosition.toString()
                    setTextColor(textColor)
                }
                tvSymbol.run {
                    text = components.structure?.symbol
                    setTextColor(textColor)
                }
                root.run {
                    background.setTint(backgroundColor)
                    setOnClickListener {
                        onItemClick?.invoke(modificationWithComponents)
                    }
                    setOnLongClickListener {
                        onItemLongClick?.invoke(modificationWithComponents)
                        onItemLongClick != null
                    }
                }
            }
        }
    }

    object ModificationUiDiffCallback :
        DiffUtil.ItemCallback<Pair<Modification, Components>>() {

        override fun areItemsTheSame(
            oldItem: Pair<Modification, Components>,
            newItem: Pair<Modification, Components>
        ) =
            oldItem.first.position == newItem.first.position

        override fun areContentsTheSame(
            oldItem: Pair<Modification, Components>,
            newItem: Pair<Modification, Components>
        ): Boolean {
            return listOf(oldItem, newItem)
                .distinctBy { (modification, components) ->
                    listOf(
                        modification.displayPosition,
                        components.material?.color,
                        components.structure?.symbol
                    )
                }
                .size == 1
        }
    }
}