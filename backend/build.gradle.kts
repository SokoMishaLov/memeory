import org.gradle.api.JavaVersion.VERSION_11
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.0.M6"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50"
    kotlin("kapt") version "1.3.50"
}

group = "ru.sokomishalov"
version = "0.0.1"
java.sourceCompatibility = VERSION_11

val developmentOnly: Configuration by configurations.creating
configurations.runtimeClasspath.get().extendsFrom(developmentOnly)

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("http://repo.spring.io/snapshot") }
    maven { url = uri("http://repo.spring.io/milestone") }
    maven { url = uri("http://oss.jfrog.org/artifactory/oss-snapshot-local") }
    maven { url = uri("http://jitpack.io") }
}

sourceSets["main"].java.srcDir(file("build/generated/source/kapt/main"))

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.github.sokomishalov.commons:commons-spring:1.0.15")

    implementation("org.mapstruct:mapstruct:1.3.0.Final")
    kapt("org.mapstruct:mapstruct-processor:1.3.0.Final")
    implementation("javax.xml.bind:jaxb-api:2.2.11")

    implementation("io.springfox:springfox-swagger2:3.0.0-SNAPSHOT")
    implementation("io.springfox:springfox-spring-webflux:3.0.0-SNAPSHOT")
    implementation("io.springfox:springfox-swagger-ui:3.0.0-SNAPSHOT")

    implementation("io.netty:netty-transport-native-epoll:4.1.41.Final")

    implementation("com.vk.api:sdk:0.5.12") {
        exclude(group = "org.apache.logging.log4j")
        exclude(group = "org.asynchttpclient")
    }
    implementation("com.github.igor-suhorukov:instagramscraper:2.2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("junit:junit:4.12")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
