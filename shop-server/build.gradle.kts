// ======================================================================
// Shop Server : Build
// ======================================================================

// Plugins
// ========================================

plugins {
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("net.ltgt.apt-eclipse")            version "0.21"
    id("io.freefair.lombok")              version "5.0.0"
    id("com.github.spotbugs")             version "4.0.4"
}

// GAV
// ========================================

group       = "griz.shop"
version     = "1.0.0-SNAPSHOT"
description = "Shop Server"

// Java Version
// ========================================

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// Application
// ========================================

application {
    mainClassName = "griz.shop.server.Application"
}

// Dependency Management
// ========================================

dependencies {
    testAnnotationProcessor(platform("io.micronaut:micronaut-bom:1.3.6"))
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
    testImplementation(platform("io.micronaut:micronaut-bom:1.3.6"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    compileOnly("org.projectlombok:lombok:1.18.12")
    annotationProcessor("org.projectlombok:lombok:1.18.12")

    annotationProcessor(platform("io.micronaut:micronaut-bom:1.3.6"))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut:micronaut-validation")

    implementation(platform("io.micronaut:micronaut-bom:1.3.6"))
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-session")
    implementation("io.micronaut.configuration:micronaut-redis-lettuce")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut.configuration:micronaut-micrometer-core")
    implementation("io.micronaut.configuration:micronaut-micrometer-registry-statsd")
    implementation("io.micronaut:micronaut-management")
    implementation("javax.annotation:javax.annotation-api")

    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
}

// Repositories
// ========================================

repositories {
    jcenter()
    mavenCentral()
}

// Code Quality
// ========================================

spotbugs {
    ignoreFailures.set(true)

    excludeFilter.set(file("spotbugs-filter-exclude.xml"))

    tasks.spotbugsMain {
        reports.create("html") {
            isEnabled = true
            setStylesheet("fancy-hist.xsl")
        }
    }

    tasks.spotbugsTest {
        reports.create("html") {
            isEnabled = true
            setStylesheet("fancy-hist.xsl")
        }
    }
}

// Tasks
// ========================================

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf("Implementation-Title" to project.name,
                  "Implementation-Vendor" to "Matt Nicholls",
                  "Implementation-Version" to project.version)
        )
    }
}

tasks.withType<JavaExec>() {
    jvmArgs("-noverify", "-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
