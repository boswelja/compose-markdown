plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.33.0")
    implementation(libs.android.libraryGradlePlugin)
    implementation(libs.kotlin.multiplatformGradlePlugin)
}
