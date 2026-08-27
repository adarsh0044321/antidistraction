package com.antigravity.antidistraction.di

import com.antigravity.antidistraction.data.repository.AppRestrictionRepositoryImpl
import com.antigravity.antidistraction.data.repository.FocusSessionRepositoryImpl
import com.antigravity.antidistraction.domain.repository.AppRestrictionRepository
import com.antigravity.antidistraction.domain.repository.FocusSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFocusSessionRepository(
        impl: FocusSessionRepositoryImpl
    ): FocusSessionRepository

    @Binds
    @Singleton
    abstract fun bindAppRestrictionRepository(
        impl: AppRestrictionRepositoryImpl
    ): AppRestrictionRepository
}
