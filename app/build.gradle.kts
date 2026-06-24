import java.util.Properties
import java.io.File
import org.gradle.api.GradleException

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

fun escapeBuildConfigString(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
}

fun clientIdFromLocalJson(properties: Properties): String {
    val configuredPath = properties.getProperty("CLASSSYNC_GOOGLE_CLIENT_SECRET_JSON").orEmpty().trim()
    if (configuredPath.isBlank()) return ""

    val file = File(configuredPath)
    if (!file.exists()) return ""

    val text = file.readText()
    if ("\"installed\"" in text && "\"web\"" !in text) {
        throw GradleException(
            """
            CLASSSYNC_GOOGLE_CLIENT_SECRET_JSON points to an installed/Android OAuth JSON, not a web client JSON.
            ClassSync needs the web OAuth client ID for Google sign-in token requests.
            Create a Web application OAuth client in the same Google Cloud project and set:
            CLASSSYNC_GOOGLE_WEB_CLIENT_ID=<your web client id>.apps.googleusercontent.com
            """.trimIndent()
        )
    }
    val regex = Regex("\"client_id\"\\s*:\\s*\"([^\"]+)\"")
    return regex.find(text)?.groupValues?.getOrNull(1).orEmpty()
}

val googleWebClientId = providers.gradleProperty("CLASSSYNC_GOOGLE_WEB_CLIENT_ID")
    .orElse(
        providers.provider {
            localProperties.getProperty("CLASSSYNC_GOOGLE_WEB_CLIENT_ID")
                ?.takeIf { it.isNotBlank() }
                ?: clientIdFromLocalJson(localProperties)
        }
    )
    .orElse(providers.environmentVariable("CLASSSYNC_GOOGLE_WEB_CLIENT_ID"))
    .get()

android {
    namespace = "com.rochiee.classsync"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rochiee.classsync"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"${escapeBuildConfigString(googleWebClientId)}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.security.crypto)
    implementation(libs.googleid)
    implementation(libs.google.play.services.auth)

    implementation(libs.google.api.client.android)
    implementation(libs.google.api.services.gmail)
    implementation(libs.google.api.services.classroom)
    implementation(libs.google.oauth.client.jetty)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
