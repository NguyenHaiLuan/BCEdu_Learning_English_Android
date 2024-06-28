plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bcedu"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bcedu"
        minSdk = 24
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.github.bumptech.glide:glide:4.16.0")

    // RxJava3
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")

    // Lottie
    implementation("com.airbnb.android:lottie:3.7.0")

    // Flip view
    implementation("com.wajahatkarim:EasyFlipView:3.0.3")

    // PaperDB
    implementation("io.github.pilgr:paperdb:2.7.2")

    // Media - ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

    // Styleable Toast
    implementation("io.github.muddz:styleabletoast:2.4.0")

    // Image Picker
    implementation("com.github.dhaval2404:imagepicker:2.1")

    // WorkManager
    implementation("androidx.work:work-runtime:2.7.0")

    // Video SDK
    implementation("live.videosdk:rtc-android-sdk:0.1.21")

    // Android Networking
    implementation("com.amitshekhar.android:android-networking:1.0.2")

    //ExoPlayer
    implementation("com.google.android.exoplayer:exoplayer:2.18.5")

    // Tap target prompt
    implementation ("com.getkeepsafe.taptargetview:taptargetview:1.13.3")

    // 1 cái tap target prompt khác - cái này là bên material
    implementation("uk.co.samuelwall:material-tap-target-prompt:3.0.0")

    implementation ("com.google.android.gms:play-services-auth:20.2.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")

}
