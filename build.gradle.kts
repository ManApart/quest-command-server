import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
}

group = "org.rak.manapart"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_14

repositories {
    mavenLocal()
    mavenCentral()
//    maven {
//        url = uri("https://maven.pkg.github.com/ManApart/quest-command")
//        credentials {
//            username = System.getenv("GITHUB_ACTOR")
//            password = System.getenv("GITHUB_TOKEN")
//        }
//    }
}

dependencies {
    implementation("io.ktor:ktor-server-core:1.6.8")
    implementation("io.ktor:ktor-server-netty:1.6.8")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    //Once stable, replace this with a reference to quest command's jar
//    implementation("org.rak.manapart:quest-command:0.0.5")
	implementation("org.rak.manapart:quest-command:SNAPSHOT")
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