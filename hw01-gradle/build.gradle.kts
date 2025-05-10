import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id ("com.github.johnrengelman.shadow")
}

dependencies {
    implementation ("com.google.guava:guava")
    implementation ("ch.qos.logback:logback-classic")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("gradleHelloOtus")
        archiveVersion.set("0.1")
        archiveClassifier.set("")
        manifest {
            attributes(mapOf("Main-Class" to "ru.otus.HelloOtus"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
