package com.myapplication

import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import root.DefaultDAGRootComponent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
        val rootComponent = DefaultDAGRootComponent(defaultComponentContext())

        setContent {
            MainView(rootComponent)
        }
    }
}