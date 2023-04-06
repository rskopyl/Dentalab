package com.rskopyl.dentalab.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rskopyl.dentalab.util.validate
import com.rskopyl.dentalab.util.validateNotNull

@Entity(
    tableName = "structure",
    indices = [Index("name")]
)
data class Structure(
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "symbol")
    val symbol: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = EMPTY_ID
) {
    init {
        validate(name.isNotBlank()) {
            Violation.BLANK_NAME
        }
        validateNotNull(symbol.singleOrNull()) {
            Violation.INVALID_SYMBOL
        }
    }

    companion object {

        const val EMPTY_ID = 0L
    }

    enum class Violation {
        BLANK_NAME, INVALID_SYMBOL
    }
}