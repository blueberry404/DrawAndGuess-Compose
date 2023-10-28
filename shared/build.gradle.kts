import com.blueberry.drawnguess.Dependencies

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

            export(Dependencies.Arkivanov.Decompose.decompose)
            export(Dependencies.Arkivanov.Essenty.lifecycle)
            export(Dependencies.Arkivanov.Essenty.stateKeeper)
            export(Dependencies.Arkivanov.Essenty.instanceKeeper)
            export(Dependencies.Arkivanov.Essenty.backHandler)
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                with (Dependencies.Arkivanov.MviKotlin) {
                    implementation(mvi)
                    implementation(main)
                    implementation(logging)
                    implementation(coroutineExt)
                }

                with (Dependencies.Arkivanov.Decompose) {
                    api(decompose)
                    api(decomposeExtJb)
                }

                with (Dependencies.Arkivanov.Essenty) {
                    api(lifecycle)
                    api(stateKeeper)
                    api(instanceKeeper)
                    api(backHandler)
                }

                with (Dependencies.Ktor) {
                    implementation(core)
                    implementation(serialization)
                    implementation(contentNegotiation)
                    implementation(logging)
                }

                with (Dependencies.Settings) {
                    implementation(noArg)
                    implementation(serialization)
                    implementation(coroutines)
                }

                implementation(Dependencies.Tools.napier)
                implementation(Dependencies.Tools.coroutineCore)

                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Koin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                with (Dependencies.Android) {
                    api(activity)
                    api(appCompat)
                    api(coreKtx)
                }
                implementation(Dependencies.Ktor.okhttp)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(Dependencies.Ktor.darwin)
                implementation(Dependencies.Ktor.ios)
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