import java.util.Properties
plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose"); id("com.google.devtools.ksp") }
val local = Properties().apply { rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) } }
val nasaKey = local.getProperty("NASA_API_KEY", "").replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "").replace("\r", "")
android {
 namespace = "pe.edu.diarioespacial"; compileSdk = 35
 defaultConfig { applicationId = "pe.edu.diarioespacial"; minSdk = 26; targetSdk = 35; versionCode = 4; versionName = "1.3"; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"; buildConfigField("String", "NASA_API_KEY", "\"$nasaKey\"") }
 buildFeatures { compose = true; buildConfig = true }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
}
ksp { arg("room.schemaLocation", "$projectDir/schemas") }
dependencies {
 implementation(platform("androidx.compose:compose-bom:2024.10.01"))
 implementation("androidx.activity:activity-compose:1.9.3")
 implementation("androidx.compose.material3:material3")
 implementation("androidx.compose.ui:ui-tooling-preview")
 implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
 implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
 implementation("androidx.navigation:navigation-compose:2.8.4")
 implementation("androidx.room:room-runtime:2.6.1")
 implementation("androidx.room:room-ktx:2.6.1")
 ksp("androidx.room:room-compiler:2.6.1")
 implementation("com.squareup.retrofit2:retrofit:2.11.0")
 implementation("com.squareup.retrofit2:converter-gson:2.11.0")
 implementation("io.coil-kt:coil-compose:2.7.0")
 implementation("org.jsoup:jsoup:1.18.3")
 testImplementation("junit:junit:4.13.2")
 testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
 androidTestImplementation("androidx.test.ext:junit:1.2.1")
 androidTestImplementation("androidx.test:runner:1.6.2")
 androidTestImplementation("androidx.room:room-testing:2.6.1")
}
