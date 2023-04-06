package com.rskopyl.dentalab.data.preferences

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

data class AppPreferences(
    val homePeriod: HomePeriod,
    val isQuickNavigationEnabled: Boolean
) {
    enum class HomePeriod(private val days: Int) {
        WEEK(days = 6),
        DEKADE(days = 10),
        FORTNIGHT(days = 14),
        MONTH(days = 30);

        fun centeredAt(date: LocalDate): List<LocalDate> {
            val start = date - DatePeriod(days = days / 2)
            return generateSequence(start) { it + DatePeriod(days = 1) }
                .take(days + 1)
                .toList()
        }
    }
}