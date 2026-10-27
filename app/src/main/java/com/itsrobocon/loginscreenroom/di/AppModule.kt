package com.itsrobocon.loginscreenroom.di

import androidx.room.Room
import com.itsrobocon.loginscreenroom.data.local.AppDatabase
import com.itsrobocon.loginscreenroom.data.repository.UserRepository
import com.itsrobocon.loginscreenroom.ui.auth.AuthViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "login_room_db"
        ).build()
    }

    single { get<AppDatabase>().userDao() }

    single { UserRepository(get()) }

    viewModelOf(::AuthViewModel)
}
