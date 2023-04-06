package com.rskopyl.dentalab.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rskopyl.dentalab.util.validate

@Entity(
    tableName = "material",
    indices = [Index("name")]
)
data class Material(
    @ColumnInfo(name = "color")
    val name: String,
    @ColumnInfo(name = "name")
    val color: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = EMPTY_ID
) {
    init {
        validate(name.isNotBlank()) {
            Violation.BLANK_NAME
        }
        validate(HEX_COLOR_REGEX.matches(color)) {
            Violation.INVALID_COLOR
        }
    }

    companion object {

        const val EMPTY_ID = 0L

        private val HEX_COLOR_REGEX = Regex("^#[0-9a-fA-F]{6}$")
    }

    enum class Violation {
        BLANK_NAME, INVALID_COLOR
    }
}