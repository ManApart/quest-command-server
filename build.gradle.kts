import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.5.20"
}

group = "org.rak.manapart"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
//	maven {
//		url = uri("https://maven.pkg.github.com/manapart/quest-command")
//	}
}

dependencies {
	implementation("io.ktor:ktor-server-core:1.6.4")
	implementation("io.ktor:ktor-server-netty:1.6.4")
	implementation("ch.qos.logback:logback-classic:1.2.3")
	implementation("org.reflections:reflections:0.9.12")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.0")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
	testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.21")
//	implementation("org.rak.manapart:quest-command:0.0.5")
	implementation("org.rak.manapart:quest-command") {
		version{
			branch = "master"
		}
	}
}

//Once stable, replace this with a reference to quest command's jar
//sourceSets.create("base") {
//	java.srcDir("../quest-command/src/main/kotlin")
//	resources.srcDir("../quest-command/src/main/resource")
//}
//
//sourceSets.getByName("main") {
//	val base = sourceSets["base"]
//	compileClasspath += base.output + base.compileClasspath
//	runtimeClasspath += output + compileClasspath
//}

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