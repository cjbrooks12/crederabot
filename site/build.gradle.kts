import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("com.eden.orchidPlugin") version "0.17.1"
    kotlin("jvm")
}

// Kotlin Setup
//----------------------------------------------------------------------------------------------------------------------
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// Orchid setup
//----------------------------------------------------------------------------------------------------------------------
repositories {
    jcenter()
}

dependencies {
    val orchid_version = "0.17.1"

    compile("io.github.javaeden.orchid:OrchidCore:$orchid_version")
    compile(files("$rootDir/lib/build/libs/lib-jvm-1.0.0.jar"))
    compile(project(":lib"))

    orchidRuntime(project)
    orchidRuntime("io.github.javaeden.orchid:OrchidCore:$orchid_version")
    orchidRuntime("io.github.javaeden.orchid:OrchidPages:$orchid_version")
    orchidRuntime("io.github.javaeden.orchid:OrchidEditorial:$orchid_version")
    orchidRuntime("io.github.javaeden.orchid:OrchidSearch:$orchid_version")
    orchidRuntime("io.github.javaeden.orchid:OrchidSwagger:$orchid_version")
    orchidRuntime("io.github.javaeden.orchid:OrchidPluginDocs:$orchid_version")
    orchidRuntime("io.github.javaeden.orchid:OrchidForms:$orchid_version")
}

project.version = "1"

orchid {
    version = "${project.version}"
    theme = "Editorial"

    if (project.hasProperty("env") && project.property("env") == "prod") {
        environment = "production"
        val isPullRequest = System.getenv("PULL_REQUEST")
        if(isPullRequest == "true") {
            baseUrl = System.getenv("DEPLOY_URL")
        }
        else if(System.getenv("URL").isNotBlank()) {
            baseUrl = System.getenv("URL")
        }
        else {
            baseUrl = "http://localhost:8080"
        }
    }
    else {
        environment = "debug"
        baseUrl = "http://localhost:8080"
    }
}

tasks.getByName("build").dependsOn("orchidBuild")