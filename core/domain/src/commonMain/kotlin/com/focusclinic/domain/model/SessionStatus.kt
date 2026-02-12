package com.focusclinic.domain.model

sealed interface SessionStatus {

    val isTerminal: Boolean

    data object Idle : SessionStatus {
        override val isTerminal = false
    }

    data object Focusing : SessionStatus {
        override val isTerminal = false
    }

    data object Completed : SessionStatus {
        override val isTerminal = true
    }

    data object Interrupted : SessionStatus {
        override val isTerminal = true
    }

    data object Cancelled : SessionStatus {
        override val isTerminal = true
    }

    data object Break : SessionStatus {
        override val isTerminal = false
    }
}
