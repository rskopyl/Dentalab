package com.rskopyl.dentalab.repository

import com.rskopyl.dentalab.data.model.Components
import com.rskopyl.dentalab.data.model.Modification
import kotlinx.coroutines.flow.Flow

interface ModificationRepository {

    fun getByOrderId(
        orderId: Long
    ): Flow<Map<Modification, Components>>

    fun getByOrderIdAndPosition(
        orderId: Long, position: Int
    ): Flow<Pair<Modification, Components>?>

    fun insert(modifications: List<Modification>)

    fun insert(modification: Modification)

    fun deleteByOrderId(orderId: Long)

    fun deleteByOrderIdAndPosition(orderId: Long, position: Int)
}