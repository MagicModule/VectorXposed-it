plugins { alias(libs.plugins.agp.lib) }

android {
    buildFeatures { aidl = true }

    buildTypes { release { isMinifyEnabled = false } }

    sourceSets {
        named("main") {
            java.directories.addAll(listOf("src/main/java"))
            aidl.directories.addAll(listOf("src/main/aidl"))
        }
    }

    aidlPackagedList += "org/matrix/vector/ipc/LoadedModule.aidl"
    namespace = "org.matrix.vector.daemonservice"
}

dependencies {
    api(libs.libxposed.itf)
    api(libs.libxposed.service)
    compileOnly(libs.androidx.annotation)
    compileOnly(libs.libxposed.annotation)
    compileOnly(projects.hiddenapi.stubs)
}
