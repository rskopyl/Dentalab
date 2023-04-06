import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id(Plugins.Android.id)
    id(Plugins.Kotlin.id)
    id(Plugins.Kapt.id)
    id(Plugins.Ksp.id)
    id(Plugins.Hilt.id)
    id(Plugins.SafeArgs.id)
}

android {
    defaultConfig {
        applicationId = Config.applicationId
        namespace = Config.namespace

        versionCode = Config.versionCode
        versionName = Config.versionName

        targetSdk = Config.targetSdk
        compileSdk = Config.compileSdk
        minSdk = Config.minSdk

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += "room.schemaLocation" to "$projectDir/schemas"
            }
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Android
    implementation(Dependencies.Android.Ktx.core)
    implementation(Dependencies.Android.AppCompat.appCompat)
    implementation(Dependencies.Android.Navigation.fragment)
    implementation(Dependencies.Android.Navigation.ui)

    // Kotlin
    implementation(Dependencies.Kotlin.Coroutines.coroutines)
    implementation(Dependencies.Kotlin.DateTime.dateTime)

    // Hilt
    implementation(Dependencies.Hilt.hilt)
    kapt(Dependencies.Hilt.compiler)

    // Interface
    implementation(Dependencies.Interface.Material.material)
    implementation(Dependencies.Interface.Constraint.constraint)
    implementation(Dependencies.Interface.ColorPicker.view)

    // Persistence
    implementation(Dependencies.Persistence.Room.runtime)
    implementation(Dependencies.Persistence.Room.ktx)
    ksp(Dependencies.Persistence.Room.compiler)
    implementation(Dependencies.Persistence.DataStore.preferences)
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.addAll(
        "-opt-in=kotlinx.coroutines.ObsoleteCoroutinesApi",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    )
}