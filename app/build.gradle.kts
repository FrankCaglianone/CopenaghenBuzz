plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "dk.itu.moapd.copenhagenbuzz.fcag"
    compileSdk = 34

    defaultConfig {
        applicationId = "dk.itu.moapd.copenhagenbuzz.fcag"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))

    // Import Firebase Authentication
    implementation("com.firebaseui:firebase-ui-auth:8.0.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-auth-ktx")

    // dotenv
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Import Real Time Database
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.firebaseui:firebase-ui-auth:8.0.0")
    implementation("com.firebaseui:firebase-ui-database:8.0.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // Google maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")


    // Firebase Storage
    implementation("com.firebaseui:firebase-ui-storage:8.0.0")
    implementation("com.google.firebase:firebase-storage-ktx")
    // Picasso for image loading
    implementation("com.squareup.picasso:picasso:2.71828")


    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")


}