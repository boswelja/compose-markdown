import java.net.URI

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.kotlinx.benchmark)

    alias(libs.plugins.detekt)

    alias(libs.plugins.dokka)
    id("com.boswelja.publish")
}

group = findProperty("group")!!
version = findProperty("version")!!

android {
    namespace = "com.boswelja.markdown"

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
            withJavadocJar()
            withSourcesJar()
        }
    }
}

kotlin {
    jvmToolchain(21)
    explicitApi()

    withSourcesJar(publish = true)

    // JVM targets
    jvm {
        compilations.create("benchmark") {
            associateWith(this@jvm.compilations.getByName("main"))
        }
    }

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
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(libs.intellij.markdown)
                implementation(libs.coil.compose)
                implementation(libs.coil.svg)
                implementation(libs.coil.gif)
                implementation(libs.coil.network.ktor)
            }
        }
        commonTest {
            dependencies {
                // Since MenuItems require icons, we need to import some to test with
                implementation(compose.materialIconsExtended)
                implementation(libs.kotlin.test)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.ktor.engine.java)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.ktor.engine.android)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.ktor.engine.darwin)
            }
        }
        getByName("jvmBenchmark") {
            dependencies {
                implementation(libs.kotlinx.benchmark)
            }
        }
    }
}

benchmark {
    targets {
        register("jvmBenchmark")
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
