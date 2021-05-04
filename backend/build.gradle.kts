plugins {
    java
}

group = "space.kappes"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json", "json", "20201115")
    implementation("org.java-websocket", "Java-WebSocket", "1.5.1")
    implementation("joda-time", "joda-time", "2.10.9")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

val fatJar = task("fatJar", type = Jar::class) {
    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File Example"
        attributes["Main-Class"] = "space.kappes.WebsocketManager"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}