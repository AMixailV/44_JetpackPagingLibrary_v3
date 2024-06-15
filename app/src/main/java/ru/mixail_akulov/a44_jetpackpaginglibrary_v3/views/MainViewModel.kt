package ru.mixail_akulov.a44_jetpackpaginglibrary_v3.views

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.User
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users.repositories.UsersRepository

@ExperimentalCoroutinesApi
@FlowPreview
class MainViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {

    val isErrorsEnabled: Flow<Boolean> = usersRepository.isErrorsEnabled()

    val usersFlow: Flow<PagingData<User>>

    private val searchBy = MutableLiveData("")

    init {
        usersFlow = searchBy.asFlow()
            // если пользователь слишком быстро набирает текст -> фильтрация промежуточных значений, чтобы избежать избыточной нагрузки
            .debounce(500)
            .flatMapLatest {
                usersRepository.getPagedUsers(it)
            }
            // всегда используйте оператор cacheIn для потоков, возвращаемых пейджером.
            // В противном случае исключение может быть выдано при
            // 1) обновлении, аннулировании или
            // 2) подписке на поток более одного раза.
            .cachedIn(viewModelScope)
    }

    fun setSearchBy(value: String) {
        if (this.searchBy.value == value) return
        this.searchBy.value = value
    }

    fun refresh() {
        this.searchBy.postValue(this.searchBy.value)
    }

    fun setEnableErrors(value: Boolean) {
        // вызывается при изменении значения флажка «Включить ошибки»
        usersRepository.setErrorsEnabled(value)
    }

}