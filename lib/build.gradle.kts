plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization") version "1.3.41"
    id("maven-publish")
}

repositories {
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

group = "com.crederabot"
version = "1.0.0"


kotlin {
    jvm()
    js {
        nodejs()
        browser()

        browser {
            testTask {
                useKarma {
                    useNodeJs()
                    useChromeHeadless()
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib-common"))
                api("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.11.0")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.0")
            }
        }

        val commonTest by getting {
            dependencies {
                api(kotlin("test-common"))
                api(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk7"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.1.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.11.0")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-js"))
            }
        }
    }
}

// Publish
//----------------------------------------------------------------------------------------------------------------------

publishing {
    repositories {
        maven(url = "${project.buildDir}/.m2/repository")
    }
}