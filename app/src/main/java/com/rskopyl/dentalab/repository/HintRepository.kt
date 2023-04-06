package com.rskopyl.dentalab.repository

import com.rskopyl.dentalab.data.model.Hint
import kotlinx.coroutines.flow.Flow

interface HintRepository {

    fun getByTarget(target: Hint.Target): Flow<List<Hint>>

    fun insert(hint: Hint)

    fun update(hint: Hint)

    fun delete(hint: Hint)
}