package com.genlz.jetpacks.utility

/**
 * A best practice that UI listen data source safely and efficiently.
 */
sealed interface UiState<out T>

object Loading : UiState<Nothing>
data class Success<out T>(val data: T) : UiState<T>
data class Fail(val throwable: Throwable) : UiState<Nothing>