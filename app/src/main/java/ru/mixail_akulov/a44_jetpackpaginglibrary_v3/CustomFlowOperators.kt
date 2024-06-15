package ru.mixail_akulov.a44_jetpackpaginglibrary_v3

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.scan

/**
 * Выдает предыдущие значения ("null", если предыдущих значений нет) вместе с текущим.
 * For example:
 * - исходный поток:
 *   ```
 *   [
 *     "a",
 *     "b",
 *     "c",
 *     "d"
 *   ]
 *   ```
 * - результирующий поток (count = 2):
 *   ```
 *   [
 *     (null, null)
 *     (null, "a"),
 *     ("a",  "b"),
 *     ("b",  "c"),
 *     ("c",  "d")
 *   ]
 *   ```
 */
@ExperimentalCoroutinesApi
fun <T> Flow<T>.simpleScan(count: Int): Flow<List<T?>> {
    val items = List<T?>(count) { null }
    return this.scan(items) { previous, value -> previous.drop(1) + value }
}