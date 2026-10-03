package com.davidferrandiz.rickandmortycharacters.data.di

import com.davidferrandiz.rickandmortycharacters.data.repository.CharacterRepositoryImpl
import com.davidferrandiz.rickandmortycharacters.data.repository.EpisodeRepositoryImpl
import com.davidferrandiz.rickandmortycharacters.domain.repository.CharacterRepository
import com.davidferrandiz.rickandmortycharacters.domain.repository.EpisodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(impl: CharacterRepositoryImpl): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindEpisodeRepository(impl: EpisodeRepositoryImpl): EpisodeRepository
}
