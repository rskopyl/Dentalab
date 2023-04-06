package com.rskopyl.dentalab.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rskopyl.dentalab.data.model.Order
import com.rskopyl.dentalab.databinding.ItemOrderBinding
import com.rskopyl.dentalab.ui.home.OrderAdapter.OrderViewHolder
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val onItemClick: ((Order) -> Unit)? = null,
) : ListAdapter<Order, OrderViewHolder>(OrderDiffCallback) {

    private val timeFormat = SimpleDateFormat(
        "k:mm", Locale.getDefault()
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        return OrderViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) = binding.run {
            tvTime.text = order.dateTime.run {
                val milliseconds = time.toMillisecondOfDay().toLong()
                timeFormat.format(Date(milliseconds))
            }
            tvPatientName.text = order.patient
            tvClinicName.text = order.customer.clinic
            root.setOnClickListener {
                onItemClick?.invoke(order)
            }
        }
    }

    object OrderDiffCallback : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Order, newItem: Order
        ): Boolean {
            return listOf(oldItem, newItem)
                .distinctBy {
                    listOf(it.dateTime, it.patient, it.customer.clinic)
                }
                .size == 1
        }
    }
}