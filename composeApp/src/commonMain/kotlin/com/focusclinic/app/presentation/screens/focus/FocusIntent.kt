package com.focusclinic.app.presentation.screens.focus

import com.focusclinic.domain.valueobject.FocusDuration

sealed interface FocusIntent {
    data class SelectDuration(val duration: FocusDuration) : FocusIntent
    data object StartSession : FocusIntent
    data object CancelSession : FocusIntent
    data object DismissResult : FocusIntent
    data object AppBackgrounded : FocusIntent
    data object AppResumed : FocusIntent
    data object DismissError : FocusIntent
}
