package com.rskopyl.dentalab.repository

import com.rskopyl.dentalab.data.model.Structure
import kotlinx.coroutines.flow.Flow

interface StructureRepository {

    fun getAll(): Flow<List<Structure>>

    fun getById(id: Long): Flow<Structure?>

    fun insert(structure: Structure)

    fun update(structure: Structure)

    fun deleteById(id: Long)
}