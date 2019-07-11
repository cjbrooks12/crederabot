plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization") version "1.3.41"
    id("maven-publish")
}

repositories {
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlinx")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx.html")
}

group = "com.crederabot"
version = "1.0.0"


kotlin {
    jvm()
    js {
        browser {
            webpackTask {
                sourceMaps = false
            }
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
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.6.12")
                implementation(npm("markdown-it"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-js"))
            }
        }

//        println("getNames=${this.getNames()}")
//
//        val jsBrowserMain by creating {
//            dependsOn(getByName("commonMain"))
//            dependsOn(jsMain)
//        }
//
//        val jsBrowserTest by getting {
//            dependsOn(jsBrowserMain)
//        }
//
//        val jsNodeMain by getting {
//            dependsOn(jsMain)
//        }
//
//        val jsNodeTest by getting {
//            dependsOn(jsNodeMain)
//        }
    }
}

// Publish
//----------------------------------------------------------------------------------------------------------------------

publishing {
    repositories {
        maven(url = "${project.buildDir}/.m2/repository")
    }
}
project.tasks["build"].dependsOn("jsBrowserWebpack")
//project.tasks["build"].dependsOn("jsNodeBrowserWebpack")
