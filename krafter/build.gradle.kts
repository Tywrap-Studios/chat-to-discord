plugins {
    kotlin("jvm") version "2.1.21"
    id("dev.kordex.gradle.kordex") version "1.6.1"
}

repositories {

}

dependencies {
    implementation(project(":common"))
    implementation(libs.kotlin.stdlib)
//    implementation(libs.slf4j)
}

kordEx {
    bot {
        mainClass = "org.tywrapstudios.krafter.AppKt"
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}