package com.davidferrandiz.rickandmortycharacters.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterEntity

@Dao
internal interface CharacterDao {

    @Query("SELECT * FROM characters ORDER BY id")
    fun pagingSource(): PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getById(id: Int): CharacterEntity?

    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    suspend fun clear()
}
