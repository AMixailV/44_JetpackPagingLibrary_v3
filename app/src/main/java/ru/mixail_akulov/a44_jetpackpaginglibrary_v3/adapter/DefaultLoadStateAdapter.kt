package ru.mixail_akulov.a44_jetpackpaginglibrary_v3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.mixail_akulov.a44_jetpackpaginglibrary_v3.databinding.PartDefaultLoadStateBinding

/**
 * Действие, выполняемое при нажатии кнопки «Попробовать еще раз»
 */
typealias TryAgainAction = () -> Unit

/**
 * Этот адаптер используется для рендеринга состояния загрузки
 * (ProgressBar, сообщения об ошибке и кнопки «Повторить попытку») в верхнем и нижнем колонтитулах списка.
 */
class DefaultLoadStateAdapter(
    private val tryAgainAction: TryAgainAction
) : LoadStateAdapter<DefaultLoadStateAdapter.Holder>() {

    override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PartDefaultLoadStateBinding.inflate(inflater, parent, false)
        return Holder(binding, null, tryAgainAction)
    }

    /**
     * Тот же макет используется для:
     * - footer
     * - main indicator
     */
    class Holder(
        private val binding: PartDefaultLoadStateBinding,
        private val swipeRefreshLayout: SwipeRefreshLayout?,
        private val tryAgainAction: TryAgainAction
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tryAgainButton.setOnClickListener { tryAgainAction() }
        }

        fun bind(loadState: LoadState) = with(binding) {
            messageTextView.isVisible = loadState is LoadState.Error
            tryAgainButton.isVisible = loadState is LoadState.Error
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.isRefreshing = loadState is LoadState.Loading
                progressBar.isVisible = false
            } else {
                progressBar.isVisible = loadState is LoadState.Loading
            }
        }
    }

}