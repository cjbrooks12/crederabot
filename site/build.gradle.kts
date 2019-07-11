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
    orchidRuntime("io.github.javaeden.orchid:OrchidDiagrams:$orchid_version")
}

project.version = "1"

orchid {
    version = "${project.version}"
    theme = "Editorial"
    environment = "debug"

    if (project.hasProperty("env") && project.property("env") in listOf("prod", "staging")) {
        environment = "production"
        if (project.property("env") == "prod") {
            if(System.getenv("CONTEXT") == "branch-deploy") {
                // branch deploys
                baseUrl = System.getenv("DEPLOY_PRIME_URL") // Netlify deploy preview URL
            }
            else if(System.getenv("PULL_REQUEST")?.toBoolean() == true) {
                // PR deploy previews
                baseUrl = System.getenv("DEPLOY_URL") // Netlify deploy preview URL
            }
            else if(System.getenv("URL")?.isNotBlank() == true) {
                // production deploys
                baseUrl = System.getenv("URL")
            }
        }
    }
}

tasks.getByName("build").dependsOn("orchidBuild")
tasks.getByName("orchidBuild").dependsOn(":lib:publish")