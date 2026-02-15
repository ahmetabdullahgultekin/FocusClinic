package com.focusclinic.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Focus : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Shop : Screen

    @Serializable
    data object Goals : Screen

    @Serializable
    data object Stats : Screen

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Settings : Screen
}
