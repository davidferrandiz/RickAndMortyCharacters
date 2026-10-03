package com.davidferrandiz.rickandmortycharacters.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

@Module
@InstallIn(SingletonComponent::class)
internal object TimeModule {

    @Provides
    fun provideClock(): Clock = Clock.systemUTC()
}
