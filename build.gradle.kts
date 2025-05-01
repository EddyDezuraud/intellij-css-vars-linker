plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.0"
}

group = "io.cssvarslinker"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.1")
    type.set("IU")
    downloadSources.set(true) // pour avoir l'autocomplétion sur les API internes
    plugins.set(listOf("com.intellij.css"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("252.*") // Pour être compatible avec 2025.1
    }

    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    buildSearchableOptions {
        enabled = false // Ce plugin ne fournit pas d'options de recherche
    }

    signPlugin {
        certificateChain.set(System.getenv("CERT_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("JETBRAINS_TOKEN")) // JetBrains Marketplace token
    }
}