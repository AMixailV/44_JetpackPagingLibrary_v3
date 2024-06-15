package ru.mixail_akulov.a44_jetpackpaginglibrary_v3.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.Repositories
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.adapter.DefaultLoadStateAdapter
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.adapter.TryAgainAction
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.adapter.UsersAdapter
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.databinding.ActivityMainBinding
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.simpleScan
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.viewModelCreator

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainLoadStateHolder: DefaultLoadStateAdapter.Holder

    private val viewModel by viewModelCreator { MainViewModel(Repositories.usersRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        Repositories.init(applicationContext)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUsersList()
        setupSearchInput()
        setupSwipeToRefresh()
        setupEnableErrorsCheckBox()
    }

    private fun setupUsersList() {
        val adapter = UsersAdapter()

        // в случае ошибок загрузки этот обратный вызов вызывается при нажатии кнопки «Повторить попытку»
        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = DefaultLoadStateAdapter(tryAgainAction)

        // комбинированный адаптер, который показывает и список пользователей + индикатор нижнего колонтитула при загрузке страниц
        val adapterWithLoadState = adapter.withLoadStateFooter(footerAdapter)

        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = adapterWithLoadState
        (binding.usersRecyclerView.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false

        mainLoadStateHolder = DefaultLoadStateAdapter.Holder(
            binding.loadStateView,
            binding.swipeRefreshLayout,
            tryAgainAction
        )

        observeUsers(adapter)
        observeLoadState(adapter)

        handleScrollingToTopWhenSearching(adapter)
        handleListVisibility(adapter)
    }

    private fun setupSearchInput() {
        binding.searchEditText.addTextChangedListener {
            viewModel.setSearchBy(it.toString())
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeUsers(adapter: UsersAdapter) {
        lifecycleScope.launch {
            viewModel.usersFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadState(adapter: UsersAdapter) {
        // вы также можете использовать adapter.addLoadStateListener
        lifecycleScope.launch {
            adapter.loadStateFlow.debounce(200).collectLatest { state ->
                // основной индикатор в центре экрана
                mainLoadStateHolder.bind(state.refresh)
            }
        }
    }

    private fun handleScrollingToTopWhenSearching(adapter: UsersAdapter) = lifecycleScope.launch {
        // список должен быть прокручен до 1-го элемента (индекс = 0), если данные были перезагружены:
        // (предыдущее состояние = загрузка, текущее состояние = не загрузка)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.usersRecyclerView.scrollToPosition(0)
                }
            }
    }

    private fun handleListVisibility(adapter: UsersAdapter) = lifecycleScope.launch {
        // список должен быть скрыт, если отображается ошибка ИЛИ если элементы загружаются после ошибки:
        // (current state = Error) OR (prev state = Error)
        //   OR
        // (before prev state = Error, prev state = NotLoading, current state = Loading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.usersRecyclerView.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun getRefreshLoadStateFlow(adapter: UsersAdapter): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }

    // ----

    private fun setupEnableErrorsCheckBox() {
        lifecycleScope.launch {
            viewModel.isErrorsEnabled.collectLatest {
                binding.errorCheckBox.isChecked = it
            }
        }
        binding.errorCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEnableErrors(isChecked)
        }
    }
}