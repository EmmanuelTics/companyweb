plugins {
    kotlin("jvm") version "1.9.10"
    application
    kotlin("plugin.serialization") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("ApplicationKt")
}

dependencies {
implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
    implementation("org.litote.kmongo:kmongo:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("io.ktor:ktor-server-status-pages:2.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("io.ktor:ktor-server-cors:2.3.4")
    implementation("io.ktor:ktor-server-html-builder:2.3.4")
    implementation("io.ktor:ktor-server-resources:2.3.4")
  
    testImplementation("io.ktor:ktor-server-tests:2.3.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
    implementation("ch.qos.logback:logback-classic:1.4.11")


   
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaExec> {
    systemProperty("file.encoding", "UTF-8")
}

tasks.register("printJavaVersion") {
    doLast {
        println("Java version: ${System.getProperty("java.version")}")
    }
}

tasks {
    shadowJar {
        archiveBaseName.set("aplicacion")
        archiveVersion.set("1.0")
        archiveClassifier.set("")
        manifest {
            attributes("Main-Class" to "ApplicationKt")
        }
    }
}

