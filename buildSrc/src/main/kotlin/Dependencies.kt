package com.blueberry.drawnguess

object Dependencies {

    object Arkivanov {
        object Decompose {
            const val decompose = "com.arkivanov.decompose:decompose:${Versions.decomposeExperimental}"
            const val decomposeExtJb = "com.arkivanov.decompose:extensions-compose-jetbrains:${Versions.decomposeExperimental}"
        }
        object Essenty {
            const val lifecycle = "com.arkivanov.essenty:lifecycle:${Versions.essenty}"
            const val stateKeeper = "com.arkivanov.essenty:state-keeper:${Versions.essenty}"
            const val instanceKeeper = "com.arkivanov.essenty:instance-keeper:${Versions.essenty}"
            const val backHandler = "com.arkivanov.essenty:back-handler:${Versions.essenty}"
        }
        object MviKotlin {
            const val mvi = "com.arkivanov.mvikotlin:mvikotlin:${Versions.mviKotlin}"
            const val main = "com.arkivanov.mvikotlin:mvikotlin-main:${Versions.mviKotlin}"
            const val logging = "com.arkivanov.mvikotlin:mvikotlin-logging:${Versions.mviKotlin}"
            const val coroutineExt = "com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:${Versions.mviKotlin}"
        }
    }

    object Ktor {
        const val core = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val serialization = "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}"
        const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:${Versions.ktor}"
        const val logging = "io.ktor:ktor-client-logging:${Versions.ktor}"

        const val okhttp = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        const val darwin = "io.ktor:ktor-client-darwin:${Versions.ktor}"
        const val ios = "io.ktor:ktor-client-ios:${Versions.ktor}"
    }

    object Tools {
        const val napier = "io.github.aakira:napier:${Versions.napier}"
        const val coroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutineCore}"
    }

    object Settings {
        const val noArg = "com.russhwolf:multiplatform-settings-no-arg:${Versions.settings}"
        const val serialization = "com.russhwolf:multiplatform-settings-serialization:${Versions.settings}"
        const val coroutines = "com.russhwolf:multiplatform-settings-coroutines:${Versions.settings}"
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
    }

    object Android {
        const val activity = "androidx.activity:activity-compose:${Versions.appCompat}"
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutineAndroid}"
    }
}