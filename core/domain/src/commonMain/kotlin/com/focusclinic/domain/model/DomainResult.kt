package com.focusclinic.domain.model

sealed interface DomainResult<out T> {

    data class Success<T>(val data: T) : DomainResult<T>

    data class Failure(val error: DomainError) : DomainResult<Nothing>

    fun <R> map(transform: (T) -> R): DomainResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> this
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }
}
