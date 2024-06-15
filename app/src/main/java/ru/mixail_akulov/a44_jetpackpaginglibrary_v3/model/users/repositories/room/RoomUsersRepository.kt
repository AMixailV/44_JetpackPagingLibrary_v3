package ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.repositories.room

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.User
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.UsersPageLoader
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.UsersPagingSource
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.repositories.UsersRepository

class RoomUsersRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val usersDao: UsersDao
) : UsersRepository {

    private val enableErrorsFlow = MutableStateFlow(false)

    override fun isErrorsEnabled(): Flow<Boolean> = enableErrorsFlow

    override fun setErrorsEnabled(value: Boolean) {
        enableErrorsFlow.value = value
    }

    override fun getPagedUsers(searchBy: String): Flow<PagingData<User>> {
        val loader: UsersPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize, searchBy)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UsersPagingSource(loader, PAGE_SIZE) }
        ).flow
    }

    private suspend fun getUsers(pageIndex: Int, pageSize: Int, searchBy: String): List<User>
            = withContext(ioDispatcher) {

        delay(2000) // некоторая задержка для проверки состояния загрузки

        // если установлен флажок «Включить ошибки» -> генерировать исключение
        if (enableErrorsFlow.value) throw IllegalStateException("Error!")

        // рассчитать значение смещения, требуемое DAO
        val offset = pageIndex * pageSize

        // get page
        val list = usersDao.getUsers(pageSize, offset, searchBy)

        // map UserDbEntity to User
        return@withContext list
            .map(UserDbEntity::toUser)
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}