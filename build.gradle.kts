buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Plugins.Android.classpath)
        classpath(Plugins.Kotlin.classpath)
        classpath(Plugins.Android.classpath)
        classpath(Plugins.Ksp.classpath)
        classpath(Plugins.Hilt.classpath)
        classpath(Plugins.SafeArgs.classpath)
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}