import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
}

group = "gg.bed"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.20")

    //kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}