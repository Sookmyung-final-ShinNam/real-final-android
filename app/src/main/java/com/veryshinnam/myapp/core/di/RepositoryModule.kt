package com.veryshinnam.myapp.core.di

import com.veryshinnam.myapp.feature.admin.data.repository.AdminRepository
import com.veryshinnam.myapp.feature.admin.data.repository.AdminRepositoryImpl
import com.veryshinnam.myapp.feature.attendance.data.repository.AttendanceRepository
import com.veryshinnam.myapp.feature.attendance.data.repository.AttendanceRepositoryImpl
import com.veryshinnam.myapp.feature.character.data.repository.CharacterRepository
import com.veryshinnam.myapp.feature.character.data.repository.CharacterRepositoryImpl
import com.veryshinnam.myapp.feature.creation.data.repository.ConversationRepository
import com.veryshinnam.myapp.feature.creation.data.repository.ConversationRepositoryImpl
import com.veryshinnam.myapp.feature.dashboard.data.repository.DashboardRepository
import com.veryshinnam.myapp.feature.dashboard.data.repository.DashboardRepositoryImpl
import com.veryshinnam.myapp.feature.home.data.repository.HomeRepository
import com.veryshinnam.myapp.feature.home.data.repository.HomeRepositoryImpl
import com.veryshinnam.myapp.feature.permit.data.repository.PermitRepository
import com.veryshinnam.myapp.feature.permit.data.repository.PermitRepositoryImpl
import com.veryshinnam.myapp.feature.settings.data.repository.UserRepository
import com.veryshinnam.myapp.feature.settings.data.repository.UserRepositoryImpl
import com.veryshinnam.myapp.feature.story.data.repository.StoryRepository
import com.veryshinnam.myapp.feature.story.data.repository.StoryRepositoryImpl
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
    abstract fun bindPermitRepository(impl: PermitRepositoryImpl): PermitRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(impl: CharacterRepositoryImpl): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(impl: ConversationRepositoryImpl): ConversationRepository

    @Binds
    @Singleton
    abstract fun bindStoryRepository(impl: StoryRepositoryImpl): StoryRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(impl: AttendanceRepositoryImpl): AttendanceRepository

    @Binds
    @Singleton
    abstract fun bindAdminRepository(impl: AdminRepositoryImpl): AdminRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository
}