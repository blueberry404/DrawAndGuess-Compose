package com.myapplication

import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import root.DefaultDAGRootComponent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponent = DefaultDAGRootComponent(defaultComponentContext())

        setContent {
            MainView(rootComponent)
        }
    }
}