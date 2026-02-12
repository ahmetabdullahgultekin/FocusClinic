package com.focusclinic.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.focusclinic.app.di.dataModule
import com.focusclinic.app.di.domainModule
import com.focusclinic.app.di.platformModule
import com.focusclinic.app.di.presentationModule
import com.focusclinic.app.navigation.BottomNavItem
import com.focusclinic.app.navigation.RootComponent
import com.focusclinic.app.navigation.Screen
import com.focusclinic.app.presentation.screens.clinic.ClinicScreen
import com.focusclinic.app.presentation.screens.clinic.ClinicViewModel
import com.focusclinic.app.presentation.screens.focus.FocusScreen
import com.focusclinic.app.presentation.screens.focus.FocusViewModel
import com.focusclinic.app.presentation.screens.shop.ShopScreen
import com.focusclinic.app.presentation.screens.shop.ShopViewModel
import com.focusclinic.app.presentation.screens.stats.StatsScreen
import com.focusclinic.app.presentation.screens.stats.StatsViewModel
import com.focusclinic.app.presentation.theme.FocusClinicTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App(
    rootComponent: RootComponent,
    koinSetup: org.koin.dsl.KoinAppDeclaration = {},
) {
    KoinApplication(application = {
        koinSetup()
        modules(platformModule(), dataModule, domainModule, presentationModule)
    }) {
        FocusClinicTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                val childStack by rootComponent.childStack.subscribeAsState()
                val activeScreen = childStack.active.configuration

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            BottomNavItem.entries.forEach { item ->
                                NavigationBarItem(
                                    selected = activeScreen == item.screen,
                                    onClick = { rootComponent.navigateTo(item.screen) },
                                    label = { Text(item.label) },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                        )
                                    },
                                )
                            }
                        }
                    },
                ) { paddingValues ->
                    Children(
                        stack = childStack,
                        modifier = Modifier.padding(paddingValues),
                    ) { child ->
                        when (child.instance) {
                            Screen.Focus -> {
                                val viewModel = koinInject<FocusViewModel>()
                                FocusScreen(viewModel = viewModel)
                            }
                            Screen.Clinic -> {
                                val clinicViewModel = koinInject<ClinicViewModel>()
                                ClinicScreen(viewModel = clinicViewModel)
                            }
                            Screen.Shop -> {
                                val shopViewModel = koinInject<ShopViewModel>()
                                ShopScreen(viewModel = shopViewModel)
                            }
                            Screen.Stats -> {
                                val statsViewModel = koinInject<StatsViewModel>()
                                StatsScreen(viewModel = statsViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
