plugins {
    id("maven-publish")
    id("signing")
}

interface CustomPublishingExtension {
    val description: Property<String>
    val repositoryUrl: Property<String>
    val license: Property<String>
}

val extension = project.extensions.create<CustomPublishingExtension>("publish")

group = findProperty("group")!!
version = findProperty("version")!!

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

afterEvaluate {
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
            val javadocJar = tasks.register("${name}JavadocJar", Jar::class) {
                archiveClassifier.set("javadoc")
                // Each archive name should be distinct. Mirror the format for the sources Jar tasks.
                archiveBaseName.set("${archiveBaseName.get()}-${name}")
            }
            artifact(javadocJar)
            pom {
                name = project.name
                description = extension.description
                url = extension.repositoryUrl.get()
                licenses {
                    license {
                        name = extension.license.get()
                        url = "${extension.repositoryUrl}/blob/main/LICENSE"
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
                    url.set(extension.repositoryUrl.get())
                }
            }
        }
    }

    // TODO: Remove after https://youtrack.jetbrains.com/issue/KT-46466 is fixed
    //  Thanks to KSoup repository for this code snippet
    tasks.withType(AbstractPublishToMaven::class).configureEach {
        dependsOn(tasks.withType(Sign::class))
    }
}
