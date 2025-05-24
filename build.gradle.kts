plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinx.benchmark) apply false

    alias(libs.plugins.dokka)
}

dependencies {
    dokka(projects.core)
    dokka(projects.material3)
}

val detektMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
    val reportTree = fileTree(baseDir = rootDir) {
        include("**/detekt/main.sarif")
    }
    input.from(reportTree)
}
