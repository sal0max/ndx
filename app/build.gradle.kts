@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
}

android {
    namespace = "de.salomax.ndx"
    compileSdk = 34

    defaultConfig {
        applicationId = namespace
        minSdk = 24
        targetSdk = 34
        // SemVer
        val major = 3
        val minor = 4
        val patch = 2
        versionCode = (major * 10000) + (minor * 100) + patch
        versionName = "$major.$minor.$patch"
        archivesName.set("$applicationId-v$versionCode")
    }

    signingConfigs {
        create("release") {
            if (getSecret("KEYSTORE_FILE") != null) {
                storeFile = File(getSecret("KEYSTORE_FILE")!!)
                storePassword = getSecret("KEYSTORE_PASSWORD")
                keyAlias = getSecret("KEYSTORE_KEY_ALIAS")
                keyPassword = getSecret("KEYSTORE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isJniDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = " [DEBUG]"
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }


    lint {
        // app doesn't provide deep-links
        disable.add("GoogleAppIndexingWarning")
        disable.add("GoogleAppIndexingApiWarning")
        //like "text_resultSmall"
        disable.add("UnusedResources")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // ViewModel and LiveData
    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycleVersion") // optional

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // SupportLibs
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.10.0")

    // Widgets
    implementation("com.github.guilhe:circular-progress-view:2.0.0")

    // LeakCanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")

    // billing
    implementation("com.android.billingclient:billing-ktx:5.2.1")

    // test
    testImplementation("junit:junit:4.13.2")
}

fun getSecret(key: String): String? {
    val secretsFile: File = rootProject.file("secrets.properties")
    return if (secretsFile.exists()) {
        val props = Properties()
        props.load(FileInputStream(secretsFile))
        props.getProperty(key)
    } else {
        null
    }
}
