plugins {
    kotlin("jvm") version "1.3.41" apply false
}

val clean by tasks.registering(Delete::class) {
    delete(
        "$buildDir/js/packages",
        "$buildDir/kotlin"
    )
}