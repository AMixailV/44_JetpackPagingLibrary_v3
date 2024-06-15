package ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.repositories.room

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UsersDao {

    @Query("SELECT * FROM users " +
            "WHERE :searchBy = '' OR name LIKE '%' || :searchBy || '%' " + // подстрока поиска
            "ORDER BY name " +  // сортировать по имени пользователя
            "LIMIT :limit OFFSET :offset") // return max :ограничить количество пользователей, начиная с позиции :offset
    suspend fun getUsers(limit: Int, offset: Int, searchBy: String = ""): List<UserDbEntity>

}