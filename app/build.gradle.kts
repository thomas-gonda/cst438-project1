import org.gradle.api.plugins.quality.Pmd

plugins {
    id("dev.detekt") version("2.0.0-alpha.6")
    id("pmd")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.cst438_project1"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.cst438_project1"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

pmd {
    toolVersion = "7.16.0"
    ruleSets = emptyList()
    ruleSetFiles = files("$rootDir/config/pmd/ruleset.xml")
    isConsoleOutput = true
}

tasks.withType<Pmd>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

detekt {
    toolVersion = "2.0.0-alpha.6"
    buildUponDefaultConfig = true
    config.setFrom(file("$rootDir/config/detekt/detekt.yml"))
    source.setFrom(
        "src/main/java",
        "src/main/kotlin",
        "src/test/java",
        "src/test/kotlin"
    )
}

tasks.named<dev.detekt.gradle.Detekt>("detekt") {
    reports {
        html.required.set(true)
        html.outputLocation.set(
            layout.buildDirectory.file("reports/detekt/detekt.html")
        )

        checkstyle.required.set(true)
        checkstyle.outputLocation.set(
            layout.buildDirectory.file("reports/detekt/detekt.xml")
        )

        sarif.required.set(true)
        sarif.outputLocation.set(
            layout.buildDirectory.file("reports/detekt/detekt.sarif")
        )
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.room.testing)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}