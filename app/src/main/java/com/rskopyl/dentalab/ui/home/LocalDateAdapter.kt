package com.rskopyl.dentalab.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rskopyl.dentalab.databinding.ItemDateBinding
import com.rskopyl.dentalab.databinding.ItemDateSelectedBinding
import com.rskopyl.dentalab.ui.home.LocalDateAdapter.LocalDateViewHolder
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import java.text.SimpleDateFormat
import java.util.*

class LocalDateAdapter(
    private val onItemClick: ((LocalDate) -> Unit)? = null,
) : RecyclerView.Adapter<LocalDateViewHolder>() {

    private var dates: List<LocalDate> = emptyList()
    private var selectedDate: LocalDate? = null

    private val dayNameFormat =
        SimpleDateFormat("EEE", Locale.getDefault())
    private val monthNameFormat =
        SimpleDateFormat("MMM", Locale.getDefault())

    @SuppressLint("NotifyDataSetChanged")
    fun submitDates(dates: List<LocalDate>) {
        this.dates = dates
        notifyDataSetChanged()
    }

    fun submitSelectedDate(selectedDate: LocalDate?) {
        val oldSelectedDate = this.selectedDate
        this.selectedDate = selectedDate

        dates.indexOf(oldSelectedDate).let { index ->
            if (index >= 0) notifyItemChanged(index)
        }
        dates.indexOf(selectedDate).let { index ->
            if (index >= 0) notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LocalDateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (ViewType.values()[viewType]) {
            ViewType.DEFAULT -> LocalDateViewHolder(
                ItemDateBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            ViewType.SELECTED -> LocalDateViewHolder(
                ItemDateSelectedBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(
        holder: LocalDateViewHolder,
        position: Int
    ) {
        val date = dates[position]
        holder.bind(date)
    }

    override fun getItemCount() = dates.size

    override fun getItemId(position: Int) =
        dates[position].toEpochDays().toLong()

    override fun getItemViewType(position: Int) =
        if (dates[position] == selectedDate) {
            ViewType.SELECTED.ordinal
        } else {
            ViewType.DEFAULT.ordinal
        }

    enum class ViewType { DEFAULT, SELECTED }

    inner class LocalDateViewHolder(
        private val root: View,
        private val tvDayName: TextView,
        private val tvDayNumber: TextView,
        private val tvMonthName: TextView,
    ) : RecyclerView.ViewHolder(root) {

        constructor(binding: ItemDateBinding) : this(
            binding.root,
            binding.tvDayName, binding.tvDayNumber, binding.tvMonthName
        )

        constructor(binding: ItemDateSelectedBinding) : this(
            binding.root,
            binding.tvDayName, binding.tvDayNumber, binding.tvMonthName
        )

        fun bind(date: LocalDate) {
            val milliseconds = date.atStartOfDayIn(TimeZone.UTC)
                .toEpochMilliseconds()
            val javaDate = Date(milliseconds)
            tvDayName.text = dayNameFormat.format(javaDate)
                .replaceFirstChar { it.uppercase() }
            tvDayNumber.text = date.dayOfMonth.toString()
            tvMonthName.text = monthNameFormat.format(javaDate)
            root.setOnClickListener {
                onItemClick?.invoke(date)
            }
        }
    }
}