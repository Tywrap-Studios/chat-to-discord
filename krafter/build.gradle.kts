import dev.kordex.gradle.plugins.kordex.DataCollection

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
        name = "JitPack"
        url = uri("https://jitpack.io")
    }
    flatDir {
        dirs("${rootProject.projectDir}/.gradle/localLibs")
    }
    maven {
        name = "QuiltMC (Snapshots)"
        url = uri("https://maven.quiltmc.org/repository/snapshot/")
        // We need this because Quilt's Maven repo is... special I guess
        metadataSources {
            gradleMetadata()
            // Ignore gradle telling us to use the .module file, stick with the POM
            ignoreGradleMetadataRedirection()
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
    api(libs.bbapi)
    // We need 1.1.0 Cozy Modules but Gradle is in a love-hate relationship with Quilt's Maven
    // I guess we're going the way of the flatDir *sigh*
    api(libs.bundles.cozy.modules)
    api(libs.bundles.database)
    api(libs.rcon)
    api(libs.excelkt)
    api(libs.bundles.ktor.jvm)
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

        voice = false
    }

    module("dev-unsafe")
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
