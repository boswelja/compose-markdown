import java.net.URI

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.detekt)

    alias(libs.plugins.dokka)
    id("com.boswelja.publish")
}

group = findProperty("group")!!
version = findProperty("version")!!

android {
    namespace = "com.boswelja.markdown.material3"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lint {
        sarifReport = true
        htmlReport = false
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    jvmToolchain(21)
    explicitApi()

    withSourcesJar(publish = true)

    // JVM targets
    jvm()

    // Android targets
    androidTarget {
        publishLibraryVariants("release")
    }

    // Apple targets
    iosArm64()

    // Web targets
    wasmJs()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":core"))
                implementation(compose.material3)
            }
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt.yml")
    basePath = rootDir.absolutePath
}

publish {
    description = "A native Compose Markdown renderer"
    repositoryUrl = "https://github.com/boswelja/compose-markdown"
    license = "MIT"
}

dokka {
    dokkaSourceSets.configureEach {
        includes.from("MODULE.md")
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl.set(URI("https://github.com/boswelja/compose-markdown/tree/main/core/src"))
            remoteLineSuffix.set("#L")
        }
    }
}
