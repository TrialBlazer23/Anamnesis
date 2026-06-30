// Root build file. Declares all plugins with `apply false` so subprojects opt in.
//
// Note (AGP 9 + Kotlin 2.4.0): AGP 9 ships built-in Kotlin pinned to KGP 2.2.10
// and will auto-upgrade lower declarations. Declaring kotlin-jvm 2.4.0 here with
// `apply false` brings the newer KGP onto the build classpath so the whole tree
// compiles against Kotlin 2.4.0. Do NOT apply org.jetbrains.kotlin.android in
// AGP 9 modules — Kotlin is built in.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}
