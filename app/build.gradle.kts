import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.alltimeowl.payrit"
    compileSdk = 34

    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())

    defaultConfig {
        applicationId = "com.alltimeowl.payrit"
        minSdk = 24
        targetSdk = 33
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "${properties["kakao_native_app_key"]}")
        buildConfigField("String", "USER_CODE", "${properties["user_code"]}")
        resValue("string", "kakao_oauth_host", "${properties["kakao_oauth_host"]}")
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // circleimageview
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // fragment
    implementation("androidx.fragment:fragment-ktx:1.5.5")

    // kakao
    implementation("com.kakao.sdk:v2-all:2.20.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation("com.kakao.sdk:v2-user:2.20.0") // 카카오 로그인 API 모듈
    implementation("com.kakao.sdk:v2-share:2.20.0") // 카카오톡 공유 API 모듈
    implementation("com.kakao.sdk:v2-talk:2.20.0") // 카카오톡 채널, 카카오톡 소셜, 카카오톡 메시지 API 모듈
    implementation("com.kakao.sdk:v2-friend:2.20.0") // 피커 API 모듈
    implementation("com.kakao.sdk:v2-navi:2.20.0") // 카카오내비 API 모듈
    implementation("com.kakao.sdk:v2-cert:2.20.0") // 카카오톡 인증 서비스 API 모듈

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")

    // firebase-messaging
    implementation("com.google.firebase:firebase-messaging-ktx")

    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // coil
    implementation("io.coil-kt:coil-svg:2.6.0")

    // dotsindicator
    implementation("com.tbuonomo:dotsindicator:5.0")

    // iamport
    implementation("com.github.iamport:iamport-android:v1.4.4")
}