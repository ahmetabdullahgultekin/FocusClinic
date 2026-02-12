package com.focusclinic.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification

@Composable
actual fun AppLifecycleObserver(
    onBackground: () -> Unit,
    onForeground: () -> Unit,
) {
    val currentOnBackground = rememberUpdatedState(onBackground)
    val currentOnForeground = rememberUpdatedState(onForeground)

    DisposableEffect(Unit) {
        val center = NSNotificationCenter.defaultCenter

        val backgroundObserver = center.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) {
            currentOnBackground.value()
        }

        val foregroundObserver = center.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) {
            currentOnForeground.value()
        }

        onDispose {
            center.removeObserver(backgroundObserver)
            center.removeObserver(foregroundObserver)
        }
    }
}
