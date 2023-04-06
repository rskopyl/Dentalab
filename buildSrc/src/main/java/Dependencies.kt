object Dependencies {

    object Android {

        object Ktx {

            private const val version = "1.9.0"
            const val core = "androidx.core:core-ktx:$version"
        }

        object AppCompat {

            private const val version = "1.6.1"
            const val appCompat = "androidx.appcompat:appcompat:$version"
        }

        object Navigation {

            private const val version = "2.5.3"

            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
        }
    }

    object Kotlin {

        object Coroutines {

            private const val version = "1.6.4"
            const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        }

        object DateTime {

            private const val version = "0.4.0"
            const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:$version"
        }
    }

    object Hilt {

        private const val version = "2.45"

        const val hilt = "com.google.dagger:hilt-android:$version"
        const val compiler = "com.google.dagger:hilt-compiler:$version"
    }

    object Interface {

        object Material {

            private const val version = "1.8.0"
            const val material = "com.google.android.material:material:$version"
        }

        object Constraint {

            private const val version = "2.1.4"
            const val constraint = "androidx.constraintlayout:constraintlayout:$version"
        }

        object ColorPicker {

            private const val version = "2.2.4"
            const val view = "com.github.skydoves:colorpickerview:$version"
        }
    }

    object Persistence {

        object Room {

            private const val version = "2.5.1"

            const val runtime = "androidx.room:room-runtime:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }

        object DataStore {

            private const val version = "1.0.0"
            const val preferences = "androidx.datastore:datastore-preferences:$version"
        }
    }
}