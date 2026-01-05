plugins {
    id("com.blacksquircle.feature")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.blacksquircle.ui.navigation"
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":core-navigation:api"))

    implementation(libs.androidx.navigation.ui)

    implementation(libs.google.dagger)
    ksp(libs.google.dagger.compiler)
}