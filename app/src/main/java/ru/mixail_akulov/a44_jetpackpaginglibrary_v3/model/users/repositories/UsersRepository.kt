package ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.repositories

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.User

interface UsersRepository {

    /**
     * Включены ли ошибки или нет.
     * Значение прослушивается нижним флажком «Включить ошибки» в MainActivity.
     */
    fun isErrorsEnabled(): Flow<Boolean>

    /**
     * Enabledisable ошибки при выборке пользователей.
     */
    fun setErrorsEnabled(value: Boolean)

    /**
     * Получить список пейджинга пользователей.
     */
    fun getPagedUsers(searchBy: String): Flow<PagingData<User>>

}