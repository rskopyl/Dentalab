package com.rskopyl.dentalab.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "modification",
    primaryKeys = ["order_id", "position"],
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["order_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Structure::class,
            parentColumns = ["id"],
            childColumns = ["structure_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Material::class,
            parentColumns = ["id"],
            childColumns = ["material_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index("order_id", "position"),
        Index("structure_id"),
        Index("material_id")
    ]
)
data class Modification(
    @ColumnInfo(name = "order_id")
    val orderId: Long,
    @ColumnInfo(name = "position")
    val position: Int,
    @ColumnInfo(name = "structure_id")
    val structureId: Long = Structure.EMPTY_ID,
    @ColumnInfo(name = "material_id")
    val materialId: Long = Material.EMPTY_ID
) {
    val displayPosition: Int
        get() = DISPLAY_POSITIONS.getValue(position)

    init {
        require(position in POSITIONS) {
            "Position is out of bounds"
        }
    }

    companion object {

        val POSITIONS = 1..32

        private val DISPLAY_POSITIONS = POSITIONS.toList()
            .chunked(size = 8)
            .mapIndexed { index, quarter ->
                if (index % 2 == 0) quarter
                else quarter.reversed()
            }
            .flatMapIndexed { index, quarter ->
                quarter.map { it - quarter.size * index }
            }
            .let { POSITIONS.zip(it).toMap() }
    }
}