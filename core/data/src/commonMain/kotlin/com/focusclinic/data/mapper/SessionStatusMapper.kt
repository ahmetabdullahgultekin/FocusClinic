package com.focusclinic.data.mapper

import com.focusclinic.domain.model.SessionStatus

private const val STATUS_FOCUSING = "FOCUSING"
private const val STATUS_COMPLETED = "COMPLETED"
private const val STATUS_INTERRUPTED = "INTERRUPTED"
private const val STATUS_CANCELLED = "CANCELLED"

fun SessionStatus.toDbString(): String = when (this) {
    SessionStatus.Idle -> STATUS_FOCUSING
    SessionStatus.Focusing -> STATUS_FOCUSING
    SessionStatus.Completed -> STATUS_COMPLETED
    SessionStatus.Interrupted -> STATUS_INTERRUPTED
    SessionStatus.Cancelled -> STATUS_CANCELLED
    SessionStatus.Break -> STATUS_COMPLETED
}

fun String.toSessionStatus(): SessionStatus = when (this) {
    STATUS_FOCUSING -> SessionStatus.Focusing
    STATUS_COMPLETED -> SessionStatus.Completed
    STATUS_INTERRUPTED -> SessionStatus.Interrupted
    STATUS_CANCELLED -> SessionStatus.Cancelled
    else -> SessionStatus.Cancelled
}
