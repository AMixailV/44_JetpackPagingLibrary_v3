package ru.mixail_akulov.a44_jetpackpaginglibrary_v3

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.AppDatabase
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.repositories.UsersRepository
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.repositories.room.RoomUsersRepository

/**
 * Содержит одноэлементные зависимости.
 */
object Repositories {

    private lateinit var applicationContext: Context

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database.db")
            .createFromAsset("initial_database.db")
            .build()
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    // ---

    val usersRepository: UsersRepository by lazy {
        RoomUsersRepository(ioDispatcher, database.getUsersDao())
    }

    /**
     * Вызовите этот метод во всех компонентах приложения, которые могут быть созданы при
     * восстановлении запуска приложения (например, в onCreate действий и служб).
     */
    fun init(context: Context) {
        applicationContext = context
    }

}