package com.focusclinic.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
actual fun AppLifecycleObserver(
    onBackground: () -> Unit,
    onForeground: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnBackground = rememberUpdatedState(onBackground)
    val currentOnForeground = rememberUpdatedState(onForeground)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> currentOnBackground.value()
                Lifecycle.Event.ON_START -> currentOnForeground.value()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
