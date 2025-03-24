import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    // Add the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")
}
android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

android {
    namespace = "com.thriber.thriverx"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.thriber.thriverx"
        minSdk = 26
        targetSdk = 34
        versionCode = 14
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        archivesName="ThriveRx-v${versionName}(${versionCode})"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildFeatures{
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources=true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}



dependencies {
    implementation("com.google.ar.sceneform:filament-android:1.17.1")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation ("androidx.compose.material:material:1.4.0") // or the latest version
    implementation ("androidx.compose.ui:ui:1.4.0") // or the latest version
    implementation ("androidx.compose.material:material-icons-extended:1.4.0")

//    implementation("androidx.compose.material3:material3-android:1.3.0")

    val nav_version = "2.7.6"
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Choose one of the following:
    // Material Design 3
    implementation("androidx.compose.material3:material3")

    // Java language implementation
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$nav_version")
//    implementation("androidx.compose.material3:material3:1.1.0")
//    implementation("androidx.compose.ui:ui:1.5.0")



// Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //camera
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    implementation ("com.github.krokyze:ucropnedit:2.2.9")
    implementation ("androidx.recyclerview:recyclerview:1.2.0")
    implementation ("com.github.bumptech.glide:glide:4.13.2")


    //google_Analytics and Fire_Base
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.google.firebase:firebase-database-ktx:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    implementation("com.google.firebase:firebase-crashlytics:18.6.1")
    implementation("com.google.firebase:firebase-auth:22.3.1")

    //pagination
    val paging_version = "3.2.1"

    implementation("androidx.paging:paging-runtime:$paging_version")

    // for pinch to zoom for the image in the gallery
    implementation ("com.github.MikeOrtiz:TouchImageView:1.4.1")

    //for graphics in compose
    implementation ("androidx.graphics:graphics-shapes:1.0.1")

    // https://mvnrepository.com/artifact/com.google.android.play/feature-delivery
    implementation("com.google.android.play:feature-delivery:2.1.0")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.11.0"))

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
}


