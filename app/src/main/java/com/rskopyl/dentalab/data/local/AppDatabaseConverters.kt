package com.rskopyl.dentalab.data.local

import androidx.room.TypeConverter
import com.rskopyl.dentalab.data.model.Hint
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

object AppDatabaseConverters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String =
        date.toString()

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String =
        dateTime.toString()

    @TypeConverter
    fun toLocalDateTime(isoString: String): LocalDateTime =
        LocalDateTime.parse(isoString)

    @TypeConverter
    fun fromHintTarget(target: Hint.Target): String =
        target.toString()

    @TypeConverter
    fun toHintTarget(enumString: String): Hint.Target =
        Hint.Target.valueOf(enumString)
}