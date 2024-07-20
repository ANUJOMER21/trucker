import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)

        kotlin("plugin.serialization") version "2.0.0"

}


kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    val ktorVersion = "1.6.4"
    val serializationVersion = "1.3.2"
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation ("androidx.compose.ui:ui:1.3.0")

            implementation ("androidx.compose.ui:ui-tooling-preview:1.3.0")
            implementation ("androidx.compose.runtime:runtime-livedata:1.3.0")
            implementation ("androidx.compose.runtime:runtime-rxjava2:1.3.0")

            implementation ("com.google.android.gms:play-services-location:18.0.0")// or the latest version

            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)

            implementation(compose.material)
            implementation("com.google.code.gson:gson:2.10.1")
            implementation(compose.ui)
            implementation ("org.json:json:20210307")
            implementation(libs.kotlin.serialization)
            implementation("io.ktor:ktor-client-json:$ktorVersion")
            implementation("io.ktor:ktor-client-logging:$ktorVersion")
            implementation(libs.kotlinx.serialization.json)
            implementation(compose.components.resources)


            implementation(compose.components.uiToolingPreview)
            implementation(libs.ktor.client.core)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.navigation.compose)
            implementation(libs.ktor.client.content.negotiation) // For JSON serialization
            implementation(libs.ktor.client.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.peekaboo.ui)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
            // peekaboo-image-picker
            implementation(libs.peekaboo.image.picker)
            api("org.lighthousegames:logging:1.5.0")
            implementation(libs.settings.coroutines)
            implementation(libs.settings)

            implementation("network.chaintech:cmp-preference:1.0.0")

            //    implementation("androidx.work:work-runtime-ktx:2.7.1")

            implementation(libs.lifecycle.viewmodel.compose)
            implementation(compose.materialIconsExtended)    }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.client.cio)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.ktor.client.ios)

        }
    }

}



android {
    namespace = "com.trucker.com"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.trucker.com"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)

    }
}
dependencies {

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.ui.android)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.trucker.com"
            packageVersion = "1.0.0"
        }
    }
}
