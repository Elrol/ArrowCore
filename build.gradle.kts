import org.codehaus.groovy.runtime.DefaultGroovyMethods.mixin

plugins {
    id("java")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    id("dev.architectury.loom") version("1.13-SNAPSHOT")
    kotlin("jvm") version "2.2.0"
}

val minecraftVersion: String by project
val fabricAPIVersion: String by project
val fabricLoaderVersion: String by project
val arrowCoreVersion: String by project
val cobblemonVersion: String by project
val prometheusVersion: String by project

group = "dev.elrol.arrow"
version = arrowCoreVersion

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }

    //AT(file("main/resources/access-transformations.txt"))
}

repositories {
    mavenCentral()
    maven(url = "https://maven.tomalbrc.de")
    maven(url = "https://maven.nucleoid.xyz")
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven(url = "https://maven.impactdev.net/repository/development/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.phoenix616.dev")
    maven(url = "https://maven.cobblemon.com")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-metadata-jvm:2.2.0")

    modImplementation("net.fabricmc:fabric-language-kotlin:1.13.8+kotlin.2.3.0")

    minecraft ("com.mojang:minecraft:1.21.1")
    mappings ("net.fabricmc:yarn:1.21.1+build.3:v2")
    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricAPIVersion}+${minecraftVersion}")
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.104.0+1.21.1"))

    modImplementation("eu.pb4:polymer-core:0.9.18+1.21.1")
    modImplementation("eu.pb4:polymer-resource-pack:0.9.18+1.21.1")
    modImplementation("eu.pb4:polymer-autohost:0.9.18+1.21.1")
    modImplementation("de.tomalbrc:filament:0.14.10+1.21.1")

    modImplementation("eu.pb4:sgui:1.6.1+1.21.1")
    modImplementation("com.cobblemon:fabric:${cobblemonVersion}+${minecraftVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    implementation("com.mysql", "mysql-connector-j","9.2.0")
    implementation("net.impactdev.impactor.api","economy","5.3.0")

    compileOnly("net.luckperms:api:5.4")
    runtimeOnly("net.luckperms:api:5.4")
    modImplementation("me.lucko:fabric-permissions-api:0.3.1")

    implementation("com.github.Chocohead:Fabric-ASM:v2.3")

    implementation("io.prometheus:simpleclient:${prometheusVersion}")
    implementation("io.prometheus:simpleclient_httpserver:${prometheusVersion}")
    implementation("io.prometheus:simpleclient_hotspot:${prometheusVersion}")

    include("io.prometheus:simpleclient:${prometheusVersion}")
    include("io.prometheus:simpleclient_httpserver:${prometheusVersion}")
    include("io.prometheus:simpleclient_hotspot:${prometheusVersion}")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(project.properties)
    }
}

fabricApi {
    configureDataGeneration()
}