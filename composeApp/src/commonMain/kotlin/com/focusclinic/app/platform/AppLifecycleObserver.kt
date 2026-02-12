package com.focusclinic.app.platform

import androidx.compose.runtime.Composable

@Composable
expect fun AppLifecycleObserver(
    onBackground: () -> Unit,
    onForeground: () -> Unit,
)
