plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.34.0")
    implementation(libs.android.libraryGradlePlugin)
    implementation(libs.kotlin.multiplatformGradlePlugin)
}
