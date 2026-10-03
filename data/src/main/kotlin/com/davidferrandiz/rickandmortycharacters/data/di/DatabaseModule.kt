package com.davidferrandiz.rickandmortycharacters.data.di

import android.content.Context
import androidx.room.Room
import com.davidferrandiz.rickandmortycharacters.data.local.RickAndMortyDatabase
import com.davidferrandiz.rickandmortycharacters.data.local.dao.CharacterDao
import com.davidferrandiz.rickandmortycharacters.data.local.dao.CharacterPagingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "rick_and_morty.db"

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RickAndMortyDatabase =
        Room.databaseBuilder(context, RickAndMortyDatabase::class.java, DATABASE_NAME).build()

    @Provides
    fun provideCharacterDao(database: RickAndMortyDatabase): CharacterDao = database.characterDao()

    @Provides
    fun provideCharacterPagingDao(database: RickAndMortyDatabase): CharacterPagingDao =
        database.characterPagingDao()
}
