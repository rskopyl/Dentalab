package com.rskopyl.dentalab.repository

import com.rskopyl.dentalab.data.model.Material
import kotlinx.coroutines.flow.Flow

interface MaterialRepository {

    fun getAll(): Flow<List<Material>>

    fun getById(id: Long): Flow<Material?>

    fun insert(material: Material)

    fun update(material: Material)

    fun deleteById(id: Long)
}