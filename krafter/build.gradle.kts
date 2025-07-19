import dev.kordex.gradle.plugins.kordex.DataCollection

plugins {
    distribution

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.detekt)

    alias(libs.plugins.kordex.plugin)
    alias(libs.plugins.ksp.plugin)
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()

        maven {
            name = "KordEx (Snapshots)"
            url = uri("https://snapshots-repo.kordex.dev")
        }
        maven {
            name = "KordEx (Releases)"
            url = uri("https://releases-repo.kordex.dev")
        }
        maven {
            name = "QuiltMC (Snapshots)"
            url = uri("https://maven.quiltmc.org/repository/snapshot/")
        }
        maven {
            name = "JitPack"
            url = uri("https://jitpack.io")
        }
    }
}

dependencies {
    detektPlugins(libs.detekt)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kx.ser)

    // Logging dependencies
    implementation(libs.groovy)
    implementation(libs.jansi)
    implementation(libs.logback)
    implementation(libs.logback.groovy)
    implementation(libs.logging)

    // Other dependencies
    implementation(libs.bbapi)
    // We need 1.1.0 Cozy Modules but Gradle is a bitch
    implementation(libs.bundles.cozy.modules)

    compileOnly(project(":common"))
}

// Configure distributions plugin
distributions {
    main {
        distributionBaseName = project.name

        contents {
            from("LICENSE")
        }
    }
}

kordEx {
    kordExVersion = libs.versions.kordex.asProvider()

    bot {
        // See https://docs.kordex.dev/data-collection.html
        dataCollection(DataCollection.Standard)

        mainClass = "org.tywrapstudios.krafter.AppKt"
    }

    module("pluralkit")
    module("func-phishing")
    module("func-mappings")
    // Currently unavailable
//    module("func-minecraft")
}

detekt {
    buildUponDefaultConfig = true

    config.from(rootProject.files("detekt.yml"))
}
