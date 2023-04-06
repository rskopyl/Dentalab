package com.rskopyl.dentalab.repository.impl

import com.rskopyl.dentalab.data.local.dao.StructureDao
import com.rskopyl.dentalab.data.model.Structure
import com.rskopyl.dentalab.di.ApplicationScope
import com.rskopyl.dentalab.repository.StructureRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StructureRepositoryImpl @Inject constructor(
    private val structureDao: StructureDao,
    @ApplicationScope
    private val applicationScope: CoroutineScope
) : StructureRepository {

    override fun getAll(): Flow<List<Structure>> =
        structureDao.getAll()

    override fun getById(id: Long): Flow<Structure?> =
        structureDao.getById(id)

    override fun insert(structure: Structure) {
        applicationScope.launch(Dispatchers.IO) {
            structureDao.insert(structure)
        }
    }

    override fun update(structure: Structure) {
        applicationScope.launch(Dispatchers.IO) {
            structureDao.update(structure)
        }
    }

    override fun deleteById(id: Long) {
        applicationScope.launch(Dispatchers.IO) {
            structureDao.deleteById(id)
        }
    }
}