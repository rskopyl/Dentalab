object Plugins {

    object Android {

        private const val version = "7.4.2"

        const val classpath = "com.android.tools.build:gradle:$version"
        const val id = "com.android.application"
    }

    object Kotlin {

        private const val version = "1.8.20"

        const val classpath = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val id = "org.jetbrains.kotlin.android"
    }

    object Kapt {

        const val id = "org.jetbrains.kotlin.kapt"
    }

    object Ksp {

        private const val version = "1.8.20-1.0.10"

        const val classpath = "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$version"
        const val id = "com.google.devtools.ksp"
    }

    object Hilt {

        private const val version = "2.45"
        const val classpath = "com.google.dagger:hilt-android-gradle-plugin:$version"
        const val id = "com.google.dagger.hilt.android"
    }

    object SafeArgs {

        private const val version = "2.5.3"

        const val classpath = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        const val id = "androidx.navigation.safeargs.kotlin"
    }
}