plugins {
    alias(libs.plugins.agp.app)
}

android {
    namespace = "org.matrix.vector.it.demo"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.matrix.vector.it.demo"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    compileOnly(projects.legacy)
}
