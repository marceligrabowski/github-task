import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed https://youtrack.jetbrains.com/issue/KTIJ-19369/False-positive-cant-be-called-in-this-context-by-implicit-receiver-with-plugins-in-Gradle-version-catalogs-as-a-TOML-file
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.manager)
    //TODO: Worth of consider building image as native - disabled as currently tests are not working with plugin
    //alias(libs.plugins.graalvm.buildtools)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

group = "pl.marceligrabowski"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

//TODO: Add healthchecks for DB/external service
//TODO: Add cache for external service
//TODO: Move to coroutines/reactive approach
//TODO: Use flyway
//TODO: Contract tests?
//TODO: Add logs

dependencies {
    implementation(platform(libs.spring.cloud.bom))
    implementation(platform(libs.testcontainers.bom))
    implementation(libs.spring.actuator)
    implementation(libs.spring.data.jpa)
    implementation(libs.spring.web)
    implementation(libs.jackson.kotlin)
    implementation(libs.flyway.core)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.spring.cloud.openfeign)
    developmentOnly(libs.spring.devtools)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.spring.test)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.mockserver)
    testImplementation(libs.mockserver.client)
    testImplementation(libs.mockk)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
