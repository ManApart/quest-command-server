import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.5.20"
}

group = "org.rak.manapart"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("io.ktor:ktor-server-core:1.6.4")
	implementation("io.ktor:ktor-server-netty:1.6.4")
	implementation("ch.qos.logback:logback-classic:1.2.3")
	//Once stable, replace this with a reference to quest command's jar
	implementation("org.rak.manapart:quest-command") {
		version{
			branch = "master"
		}
	}
}

//Needed because idea doesn't recognize the sources for a git reference
//Can be removed once we're referencing a real jar
sourceSets.getByName("main") {
	val generatedSourcesPath = file("../quest-command/build/classes/")
	java.srcDir(generatedSourcesPath)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		languageVersion = "1.5"
		jvmTarget = "11"
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