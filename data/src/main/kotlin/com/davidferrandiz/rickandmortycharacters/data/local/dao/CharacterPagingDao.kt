package com.davidferrandiz.rickandmortycharacters.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CHARACTER_PAGING_ROW_ID
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterPagingEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CharacterPagingDao {

    @Query("SELECT * FROM character_paging WHERE id = $CHARACTER_PAGING_ROW_ID")
    suspend fun get(): CharacterPagingEntity?

    @Query("SELECT totalCount FROM character_paging WHERE id = $CHARACTER_PAGING_ROW_ID")
    fun observeTotalCount(): Flow<Int?>

    @Upsert
    suspend fun upsert(paging: CharacterPagingEntity)

    @Query("DELETE FROM character_paging")
    suspend fun clear()
}
