package com.davidferrandiz.rickandmortycharacters.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davidferrandiz.rickandmortycharacters.data.local.dao.CharacterDao
import com.davidferrandiz.rickandmortycharacters.data.local.dao.CharacterPagingDao
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterEntity
import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterPagingEntity

@Database(
    entities = [CharacterEntity::class, CharacterPagingEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(EpisodeIdsConverter::class)
internal abstract class RickAndMortyDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun characterPagingDao(): CharacterPagingDao
}
