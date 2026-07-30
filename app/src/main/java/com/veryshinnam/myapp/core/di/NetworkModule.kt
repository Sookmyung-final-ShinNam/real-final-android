package com.veryshinnam.myapp.core.di

import com.veryshinnam.myapp.core.network.AuthInterceptor
import com.veryshinnam.myapp.core.network.BaseUrls
import com.veryshinnam.myapp.feature.admin.data.api.AdminApi
import com.veryshinnam.myapp.feature.attendance.data.api.AttendanceApi
import com.veryshinnam.myapp.feature.character.data.api.CharacterApi
import com.veryshinnam.myapp.feature.creation.data.api.ConversationApi
import com.veryshinnam.myapp.feature.dashboard.data.api.DashboardApi
import com.veryshinnam.myapp.feature.home.data.api.HomeApi
import com.veryshinnam.myapp.feature.permit.data.api.PermitApi
import com.veryshinnam.myapp.feature.settings.data.api.UserApi
import com.veryshinnam.myapp.feature.story.data.api.StoryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// api 기본 요청
// Retrofit + OkHttpClient
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule  {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // 액세스 토큰 자동 주입
            .connectTimeout(30, TimeUnit.SECONDS) // 서버 연결 최대 30초 대기
            .readTimeout(30, TimeUnit.SECONDS)    // 응답 수신 최대 30초 대기
            .writeTimeout(30, TimeUnit.SECONDS)   // 요청 전송 최대 30초 대기
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BaseUrls.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환
            .build()

    // Api 구현체 생성
    // ex) api.login() > PATCH /api/permit/login
    @Provides
    @Singleton
    fun providePermitApi(retrofit: Retrofit): PermitApi =
        retrofit.create(PermitApi::class.java)

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi =
        retrofit.create(HomeApi::class.java)

    @Provides
    @Singleton
    fun provideCharacterApi(retrofit: Retrofit): CharacterApi =
        retrofit.create(CharacterApi::class.java)

    @Provides
    @Singleton
    fun provideConversationApi(retrofit: Retrofit): ConversationApi =
        retrofit.create(ConversationApi::class.java)

    @Provides
    @Singleton
    fun provideStoryApi(retrofit: Retrofit): StoryApi =
        retrofit.create(StoryApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideAttendanceApi(retrofit: Retrofit): AttendanceApi =
        retrofit.create(AttendanceApi::class.java)

    @Provides
    @Singleton
    fun provideAdminApi(retrofit: Retrofit): AdminApi =
        retrofit.create(AdminApi::class.java)

    @Provides
    @Singleton
    fun provideDashboardApi(retrofit: Retrofit): DashboardApi =
        retrofit.create(DashboardApi::class.java)
}