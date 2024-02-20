import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.net.URL

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)

    alias(libs.plugins.detekt)

    alias(libs.plugins.dokka)

    id("maven-publish")
    id("signing")
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
    jvmToolchain(17)
    explicitApi()

    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(libs.intellij.markdown)
                implementation(libs.coil.compose)
                implementation(libs.coil.svg)
                implementation(libs.coil.gif)
            }
        }
        commonTest {
            dependencies {
                // Since MenuItems require icons, we need to import some to test with
                implementation(compose.materialIconsExtended)
                implementation(libs.kotlin.test)
            }
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt.yml")
    basePath = rootDir.absolutePath
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

publishing {
    repositories {
        if (System.getenv("PUBLISHING") == "true") {
            maven("https://maven.pkg.github.com/boswelja/compose-markdown") {
                val githubUsername: String? by project.properties
                val githubToken: String? by project.properties
                name = "github"
                credentials {
                    username = githubUsername
                    password = githubToken
                }
            }
            maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                val ossrhUsername: String? by project
                val ossrhPassword: String? by project
                name = "oss"
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }

    publications.withType<MavenPublication> {
        pom {
            name = "core"
            description = " A native Compose Markdown renderer"
            url = "https://github.com/boswelja/compose-markdown"
            licenses {
                license {
                    name = "MIT"
                    url = "https://github.com/boswelja/compose-markdown/blob/main/LICENSE"
                }
            }
            developers {
                developer {
                    id = "boswelja"
                    name = "Jack Boswell (boswelja)"
                    email = "boswelja@outlook.com"
                    url = "https://github.com/boswelja"
                }
            }
            scm {
                connection.set("scm:git:github.com/boswelja/compose-markdown.git")
                developerConnection.set("scm:git:ssh://github.com/boswelja/compose-markdown.git")
                url.set("https://github.com/boswelja/compose-markdown")
            }
        }
    }
}

tasks.withType<DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        includes.from("MODULE.md")
        externalDocumentationLink(
            url = "https://developer.android.com/reference/kotlin/",
            packageListUrl = "https://developer.android.com/reference/kotlin/androidx/package-list"
        )
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl.set(URL("https://github.com/boswelja/compose-markdown/tree/main/core/src"))
            remoteLineSuffix.set("#L")
        }
    }
}
