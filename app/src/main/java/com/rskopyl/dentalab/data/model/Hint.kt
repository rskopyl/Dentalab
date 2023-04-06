package com.rskopyl.dentalab.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.rskopyl.dentalab.util.validate

@Entity(
    tableName = "hint",
    primaryKeys = ["text", "target"],
    indices = [Index("text", "target")]
)
data class Hint(
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "target")
    val target: Target
) {
    init {
        validate(text.isNotBlank()) {
            Violation.BLANK_TEXT
        }
    }

    enum class Target { CLINIC, DOCTOR }

    enum class Violation { BLANK_TEXT }
}