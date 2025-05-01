plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.15.0"
}

group = "io.cssvarslinker"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2025.1") // Version IntelliJ cible
    type.set("IC") // IC = IntelliJ Community Edition, IU = Ultimate
    plugins.set(listOf("css")) // Nécessaire pour interagir avec les fichiers CSS
}

tasks {
    patchPluginXml {
        sinceBuild.set("251")          // 2025.1 → build 251.xxx
        untilBuild.set("252.*")
    }

    buildSearchableOptions {
        enabled = false // Ce plugin n’ajoute pas d’options UI
    }

    signPlugin {
        certificateChain.set(System.getenv("CERT_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("JETBRAINS_TOKEN")) // JetBrains Marketplace token (optionnel)
    }
}