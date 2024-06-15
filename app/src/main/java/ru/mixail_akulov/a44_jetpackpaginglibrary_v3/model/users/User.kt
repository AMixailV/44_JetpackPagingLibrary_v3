package ru.mixail_akulov.a44_jetpackpaginglibrary_v3.model.users

/**
 * Класс для представления пользовательских данных в приложении.
 */
data class User(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val company: String
)