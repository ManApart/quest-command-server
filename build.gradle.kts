import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}

group = "org.rak.manapart"
version = ""
java.sourceCompatibility = JavaVersion.VERSION_14

val props = loadProps("hidden-gradle.properties")
fun loadProps(fileName: String): Map<String, String>{
    val propsFile = rootProject.file(fileName)
    return if (propsFile.exists()) {
        propsFile.readLines().associate { line ->
            val parts = line.split("=").map { it.trim() }
            parts.first()to  parts.last()
        }
    } else {
        println(propsFile.name + "  does not exist")
        mapOf()
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/ManApart/quest-command")
        credentials {
            username = props["gpr.user"]
            password = props["gpr.key"]
        }
    }
}


dependencies {
    val ktor = "2.0.2"
    implementation("io.ktor:ktor-server-core:$ktor")
    implementation("io.ktor:ktor-server-netty:$ktor")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
    implementation("io.ktor:ktor-server-cors:$ktor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("ch.qos.logback:logback-classic:1.2.11")
	implementation("org.rak.manapart:quest-command:dev") {
//    implementation("org.rak.manapart:quest-command:0.0.6") {
        exclude("org.jetbrains.kotlin","kotlin-stdlib")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        languageVersion = "1.5"
        jvmTarget = "14"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

}