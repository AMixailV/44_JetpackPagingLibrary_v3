package ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.repositories.room.UserDbEntity
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.repositories.room.UsersDao

@Database(
    version = 1,
    entities = [
        UserDbEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUsersDao(): UsersDao

}