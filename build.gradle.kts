import org.jetbrains.changelog.closure;

fun properties(key: String) = project.findProperty(key).toString()

plugins {
  // Java support
  id("java")
  // Kotlin support
  id("org.jetbrains.kotlin.jvm") version "1.5.10"
  // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
  id("org.jetbrains.intellij") version "1.0"
  // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
  id("org.jetbrains.changelog") version "1.1.2"
  // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
  id("io.gitlab.arturbosch.detekt") version "1.17.1"
  // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
  id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
  mavenCentral()
  maven(url = "https://maven-central.storage-download.googleapis.com/repos/central/data/")
  maven(url = "https://www.jetbrains.com/intellij-repository/releases")
  maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
  maven(url = "https://cache-redirector.jetbrains.com/intellij-dependencies")
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")
  implementation("com.thoughtworks.xstream:xstream:1.4.16")
  implementation("org.javassist:javassist:3.27.0-GA")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
  pluginName.set(properties("pluginName"))
  version.set(properties("platformVersion"))
  type.set(properties("platformType"))
  downloadSources.set(true)
  instrumentCode.set(true)
  updateSinceUntilBuild.set(true)
//  localPath.set(properties("idePath"))

}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
  path = "${project.projectDir}/changelog/CHANGELOG.md"
  version = properties("pluginVersion")
  header = closure { version }
  itemPrefix = "-"
  keepUnreleasedSection = true
  unreleasedTerm = "Changelog"
  groups = listOf("Features", "Fixes", "Removals", "Other")
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
  config = files("./detekt-config.yml")
  buildUponDefaultConfig = true

  reports {
    html.enabled = false
    xml.enabled = false
    txt.enabled = false
  }
}

tasks {
  // Set the compatibility versions to 1.8
  withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xskip-runtime-version-check"
    kotlinOptions.languageVersion = properties("kotlinLangVersion")
    kotlinOptions.apiVersion = properties("kotlinApiVersion")
  }

  withType<io.gitlab.arturbosch.detekt.Detekt> {
    jvmTarget = "1.8"
  }

  sourceSets {
    main {
      java.srcDirs("src/main/java")
      resources.srcDirs("src/main/resources")
    }
  }

  patchPluginXml {
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))

    // Get the latest available change notes from the changelog file
//    changeNotes.set(
//      changelog.getLatest().toHTML()
//    )
  }

  runPluginVerifier {
    ideVersions.set(properties("pluginVerifierIdeVersions").split(',').map { it.trim() }.toList())
  }

  buildSearchableOptions {
    enabled = false
  }

  publishPlugin {
//    dependsOn("patchChangelog")
    token.set(file("./publishToken").readText())
  }
}
