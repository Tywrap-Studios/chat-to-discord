import dev.kordex.gradle.plugins.kordex.DataCollection
import dev.kordex.gradle.plugins.kordex.base.latestKordMetadata

plugins {
    distribution

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.detekt)

    alias(libs.plugins.kordex.plugin)
    alias(libs.plugins.ksp.plugin)
}

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
    flatDir {
        dirs("${rootProject.projectDir}/.gradle/localLibs")
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
    // We need 1.1.0 Cozy Modules but Gradle is in a love-hate relationship with Quilt's Maven
    implementation(libs.bundles.cozy.modules)
    // I guess we're going the way of the flatDir *sigh*
//    implementation(":module-ama:1.1.0-SNAPSHOT")
//    implementation(":module-log-parser:1.1.0-SNAPSHOT")
//    implementation(":module-moderation:1.1.0-SNAPSHOT")
    api(libs.bundles.database)

//    implementation(project(":common"))
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
    module("func-tags")
    module("func-welcome")
    // Currently unavailable
//    module("func-minecraft")

    i18n {
        classPackage = "org.tywrapstudios.krafter.i18n"
        translationBundle = "krafter.strings"
        outputDirectory = File("${project.projectDir}/src/main/kotlin")
    }
}

detekt {
    buildUponDefaultConfig = true

    config.from(rootProject.files("detekt.yml"))
}
