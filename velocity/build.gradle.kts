/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("com.ranull.java-conventions")
}

dependencies {
    compileOnly(project(":common"))
    compileOnly("com.github.Moulberry:adventure-binary-serializer:master-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.velocitypowered", "velocity-api", "3.2.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered", "velocity-api", "3.2.0-SNAPSHOT")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
