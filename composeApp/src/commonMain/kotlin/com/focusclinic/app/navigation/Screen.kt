package com.focusclinic.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Focus : Screen

    @Serializable
    data object Clinic : Screen

    @Serializable
    data object Shop : Screen

    @Serializable
    data object Goals : Screen

    @Serializable
    data object Stats : Screen
}
