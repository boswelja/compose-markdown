import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish")
}

interface CustomPublishingExtension {
    val description: Property<String>
    val repositoryUrl: Property<String>
    val license: Property<String>
}

val extension = project.extensions.create<CustomPublishingExtension>("publish")

group = findProperty("group")!!
version = findProperty("version")!!

afterEvaluate {
    publishing {
        repositories {
            maven("https://maven.pkg.github.com/boswelja/compose-markdown") {
                name = "github"
                credentials(PasswordCredentials::class)
            }
        }
    }

    mavenPublishing {
        coordinates(group as String, name, version as String)

        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
        signAllPublications()

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
