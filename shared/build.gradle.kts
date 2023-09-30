plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("kotlin-parcelize")
    kotlin("plugin.serialization")
}

kotlin {
    android()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true

            export("com.arkivanov.decompose:decompose:2.0.0-compose-experimental-alpha-02")
            export("com.arkivanov.essenty:lifecycle:1.1.0")
            export("com.arkivanov.essenty:state-keeper:1.1.0")
            export("com.arkivanov.essenty:instance-keeper:1.1.0")
            export("com.arkivanov.essenty:back-handler:1.1.0")
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    sourceSets {
        val ktor = "2.3.0"
        val koin = "3.4.0"

        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                val mviKotlin = "3.2.0"
                implementation("com.arkivanov.mvikotlin:mvikotlin:$mviKotlin")
                implementation("com.arkivanov.mvikotlin:mvikotlin-main:$mviKotlin")
                implementation("com.arkivanov.mvikotlin:mvikotlin-logging:$mviKotlin")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$mviKotlin")

                api("com.arkivanov.decompose:decompose:2.0.0-compose-experimental-alpha-02")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:2.0.0-compose-experimental-alpha-02")
                api("com.arkivanov.essenty:lifecycle:1.1.0")
                api("com.arkivanov.essenty:state-keeper:1.1.0")
                api("com.arkivanov.essenty:instance-keeper:1.1.0")
                api("com.arkivanov.essenty:back-handler:1.1.0")

                implementation("io.ktor:ktor-client-core:$ktor")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
                implementation("io.ktor:ktor-client-content-negotiation:$ktor")
                implementation("io.ktor:ktor-client-logging:$ktor")

                implementation("io.github.aakira:napier:2.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

                val settings = "1.0.0"
                implementation("com.russhwolf:multiplatform-settings-no-arg:$settings")
                implementation("com.russhwolf:multiplatform-settings-serialization:$settings")
                implementation("com.russhwolf:multiplatform-settings-coroutines:$settings")

                implementation("io.insert-koin:koin-core:$koin")
                implementation("io.insert-koin:koin-test:$koin")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.6.1")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.9.0")
                implementation("io.ktor:ktor-client-okhttp:$ktor")
                implementation("com.squareup.okhttp3:okhttp:4.10.0")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktor")
                implementation("io.ktor:ktor-client-ios:$ktor")
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}