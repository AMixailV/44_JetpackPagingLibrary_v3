package ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users

import androidx.paging.PagingSource
import androidx.paging.PagingState

typealias UsersPageLoader = suspend (pageIndex: Int, pageSize: Int) -> List<User>

/**
 * Example implementation of [PagingSource].
 * It is used by [Pager] for fetching data.
 */
@Suppress("UnnecessaryVariable")
class UsersPagingSource(
    private val loader: UsersPageLoader,
    private val pageSize: Int
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        // получить индекс загружаемой страницы (может быть NULL, в этом случае загрузим первую страницу с индексом = 0)
        val pageIndex = params.key ?: 0

        return try {
            // загрузка нужной страницы пользователей
            val users = loader.invoke(pageIndex, params.loadSize)
            // успех! теперь мы можем вернуться LoadResult.Page
            return LoadResult.Page(
                data = users,
                // индекс предыдущей страницы, если существует
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                // индекс следующей страницы, если он существует;
                // обратите внимание, что params.loadSize может быть больше для первой загрузки (по умолчанию в 3 раза)
                nextKey = if (users.size == params.loadSize) pageIndex + (params.loadSize / pageSize) else null
            )
        } catch (e: Exception) {
            // не удалось загрузить пользователей -> необходимо вернуть LoadResult.Error
            LoadResult.Error(
                throwable = e
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        // получить последний использованный индекс в списке пользователей:
        val anchorPosition = state.anchorPosition ?: return null
        // преобразовать индекс элемента в индекс страницы:
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        // страница не имеет свойства currentKey, поэтому его необходимо вычислить вручную:
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

}