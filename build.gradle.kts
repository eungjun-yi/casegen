import com.adarshr.gradle.testlogger.theme.ThemeType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.3.50"

buildscript {
    val kotlinVersion = "1.3.50"
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    java
    maven
    jacoco
    idea

    id("com.adarshr.test-logger") version "1.6.0"
}

apply {
    plugin("kotlin")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testImplementation("com.github.toss:assert-extensions:0.2.0")
}

repositories {
    maven { url = uri("https://jitpack.io") }
    mavenCentral()
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks

compileTestKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "1.8"
}

jacoco {
    toolVersion = "0.8.4"
}

tasks {
    test {
        useJUnitPlatform {
        }
    }

    jacocoTestReport {
        executionData.setFrom(
            fileTree("build/jacoco") {
                include("**/*.exec")
            }
        )

        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }

    jacocoTestCoverageVerification {
        dependsOn(setOf(jacocoTestReport))
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    minimum = "1.000000000".toBigDecimal()
                }

                limit {
                    counter = "BRANCH"
                    minimum = "1.000000000".toBigDecimal()
                }
            }
        }
    }
}

testlogger {
    theme = ThemeType.STANDARD_PARALLEL
    showExceptions = true
    slowThreshold = 2000
    showSummary = true
    showPassed = true
    showSkipped = true
    showFailed = true
    showStandardStreams = false
    showPassedStandardStreams = false
    showSkippedStandardStreams = false
    showFailedStandardStreams = false
}

