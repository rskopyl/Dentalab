package com.rskopyl.dentalab.ui.formula

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rskopyl.dentalab.data.model.Material
import com.rskopyl.dentalab.databinding.ItemModificationMaterialBinding
import com.rskopyl.dentalab.ui.formula.MaterialAdapter.ModificationMaterialViewHolder

class MaterialAdapter : ListAdapter<Material, ModificationMaterialViewHolder>(
    ModificationTypeDiffCallback
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModificationMaterialViewHolder {
        return ModificationMaterialViewHolder(
            ItemModificationMaterialBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ModificationMaterialViewHolder,
        position: Int
    ) {
        val type = getItem(position)
        holder.bind(type)
    }

    class ModificationMaterialViewHolder(
        private val binding: ItemModificationMaterialBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(type: Material) = binding.tvName.run {
            text = type.name
            compoundDrawablesRelative.first().setTint(
                try {
                    Color.parseColor(type.color)
                } catch (e: IllegalArgumentException) {
                    Color.TRANSPARENT
                }
            )
            postDelayed(1000L) {
                isSelected = true
            }
        }
    }

    object ModificationTypeDiffCallback :
        DiffUtil.ItemCallback<Material>() {

        override fun areItemsTheSame(oldItem: Material, newItem: Material) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Material, newItem: Material
        ): Boolean {
            return listOf(oldItem, newItem)
                .distinctBy { listOf(it.name, it.color) }
                .size == 1
        }
    }
}