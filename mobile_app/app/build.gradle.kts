import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // room support
    id ("com.google.devtools.ksp")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.ssafy.waybackhome"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ssafy.waybackhome"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "NAVER_API_KEY", getPropertyByKey("NAVER_API_KEY"))
        buildConfigField("String", "NAVER_API_ID", getPropertyByKey("NAVER_API_ID"))
        buildConfigField("String", "SERVER_URL", getPropertyByKey("SERVER_URL"))
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

// local.properties
fun getPropertyByKey(key : String) : String {
    return gradleLocalProperties(rootDir).getProperty(key)
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")


    // 네이버 지도 SDK
    implementation("com.naver.maps:map-sdk:3.18.0")

    // gms - FusedLocationPorvider
    implementation ("com.google.android.gms:play-services-location:21.2.0")

    // room
    //Room 의존성 추가
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler-processing:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")


    // retrofit
    // https://github.com/square/retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    // gson
    // https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // okhttp
    // https://github.com/square/okhttp
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    // https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // viewModels
    implementation("androidx.fragment:fragment-ktx:1.6.2")
}