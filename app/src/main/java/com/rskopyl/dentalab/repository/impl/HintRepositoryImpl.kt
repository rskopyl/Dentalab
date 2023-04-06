package com.rskopyl.dentalab.repository.impl

import com.rskopyl.dentalab.data.local.dao.HintDao
import com.rskopyl.dentalab.data.model.Hint
import com.rskopyl.dentalab.di.ApplicationScope
import com.rskopyl.dentalab.repository.HintRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HintRepositoryImpl @Inject constructor(
    private val hintDao: HintDao,
    @ApplicationScope
    private val applicationScope: CoroutineScope
) : HintRepository {

    override fun getByTarget(target: Hint.Target): Flow<List<Hint>> =
        hintDao.getByTarget(target)

    override fun insert(hint: Hint) {
        applicationScope.launch(Dispatchers.IO) {
            hintDao.insert(hint)
        }
    }

    override fun update(hint: Hint) {
        applicationScope.launch(Dispatchers.IO) {
            hintDao.update(hint)
        }
    }

    override fun delete(hint: Hint) {
        applicationScope.launch(Dispatchers.IO) {
            hintDao.delete(hint)
        }
    }
}