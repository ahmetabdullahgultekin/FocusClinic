package com.focusclinic.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.focusclinic.app.navigation.RootComponent
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val rootComponent = RootComponent(defaultComponentContext())

        setContent {
            App(
                rootComponent = rootComponent,
                koinSetup = { androidContext(this@MainActivity.applicationContext) },
            )
        }
    }
}
