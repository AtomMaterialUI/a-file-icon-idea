/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Elior "Mallowigi" Boukhobza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
@file:Suppress("SpellCheckingInspection", "HardCodedStringLiteral")

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/** Get a property from the gradle.properties file. */
fun properties(key: String) = providers.gradleProperty(key).get()

/**
 * Returns the value of the environment variable associated with the specified key.
 *
 * @param key the key of the environment variable
 * @return the value of the environment variable as a Provider<String>
 */
fun environment(key: String) = providers.environmentVariable(key)

/** Get a property from a file. */
fun fileProperties(key: String) = project.findProperty(key).toString().let { if (it.isNotEmpty()) file(it) else null }

plugins {
  signing
  // Java support
  id("java")
  alias(libs.plugins.kotlin)
  alias(libs.plugins.gradleIntelliJPlugin)
  alias(libs.plugins.changelog)
  alias(libs.plugins.qodana)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.kover)
}


dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.4")
  implementation("com.jgoodies:jgoodies-forms:1.9.0")
  implementation("com.thoughtworks.xstream:xstream:1.4.20")
  implementation("org.javassist:javassist:3.29.2-GA")
  implementation(project(":common"))
  runtimeOnly(project(":rider"))
}


group = properties("pluginGroup")
version = properties("pluginVersion")

allprojects {
  apply {
    plugin("java")
    plugin("org.jetbrains.kotlin.jvm")
    plugin("org.jetbrains.intellij")
  }

  repositories {
    mavenCentral()
    maven(url = "https://maven-central.storage-download.googleapis.com/repos/central/data/")
    maven(url = "https://repo.eclipse.org/content/groups/releases/")
    maven(url = "https://www.jetbrains.com/intellij-repository/releases")
    maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
  }

  java {
    toolchain {
      languageVersion = JavaLanguageVersion.of(17)
    }
  }

  kotlin {
    jvmToolchain(17)
  }

  tasks {
    properties("javaVersion").let {
      // Set the compatibility versions to 1.8
      withType<JavaCompile> {
        sourceCompatibility = it
        targetCompatibility = it
      }
      withType<KotlinCompile> {
        kotlinOptions.jvmTarget = it
        kotlinOptions.freeCompilerArgs += listOf("-Xskip-prerelease-check", "-Xjvm-default=all")
      }
    }


    withType<Copy> {
      duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    sourceSets {
      main {
        java.srcDirs("src/main/java")
        resources.srcDirs("src/main/resources")
      }
    }

    buildSearchableOptions {
      enabled = false
    }

  }
}

koverReport {
  defaults {
    xml {
      onCheck = true
    }
  }
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
  pluginName = properties("pluginName")
  version = properties("platformVersion")
  type = properties("platformType")
  downloadSources = true
  instrumentCode = true
  updateSinceUntilBuild = true
//  plugins
//    listOf(
//      "zielu.gittoolbox:213.10.3"
//    )
//  )
  //  localPath = properties("idePath")
//  sandboxDir = "/Applications/apps/datagrip/ch-1/212.4416.10/DataGrip 2021.2 EAP.app"
}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
  path = "${project.projectDir}/docs/CHANGELOG.md"
  version = properties("pluginVersion")
  header = provider { version.get() }
  headerParserRegex = "(\\d+\\.\\d+\\.\\d+)"
  itemPrefix = "-"
  keepUnreleasedSection = true
  unreleasedTerm = "Changelog"
  groups = listOf("Features", "Fixes", "Removals", "Additions", "Other")
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
  config.setFrom("./detekt-config.yml")
  buildUponDefaultConfig = true
  autoCorrect = true
  ignoreFailures = true
}

// Configure Gradle Qodana Plugin - read more: https://github.com/JetBrains/gradle-qodana-plugin
qodana {
  cachePath = provider { file(".qodana").canonicalPath }
  reportPath = provider { file("build/reports/inspections").canonicalPath }
  saveReport = true
  showReport = environment("QODANA_SHOW_REPORT").map { it.toBoolean() }.getOrElse(false)
}

tasks {

  wrapper {
    gradleVersion = properties("gradleVersion")
  }

  withType<Detekt> {
    jvmTarget = properties("javaVersion")
    reports.xml.required = true
  }


  patchPluginXml {
    version = properties("pluginVersion")
    sinceBuild = properties("pluginSinceBuild")
    untilBuild = properties("pluginUntilBuild")

    // Get the latest available change notes from the changelog file
    changeNotes = changelog.renderItem(changelog.getLatest(), Changelog.OutputType.HTML)
  }

  runPluginVerifier {
    ideVersions = properties("pluginVerifierIdeVersions").split(',').map { it.trim() }.toList()
  }

  // Configure UI tests plugin
  // Read more: https://github.com/JetBrains/intellij-ui-test-robot
  runIdeForUiTests {
    systemProperty("robot-server.port", "8082")
    systemProperty("ide.mac.message.dialogs.as.sheets", "false")
    systemProperty("jb.privacy.policy.text", "<!--999.999-->")
    systemProperty("jb.consents.confirmation.enabled", "false")
  }

//  runIde {
//    jvmArgs = properties("jvmArgs").split("")
//    systemProperty("jb.service.configuration.url", properties("salesUrl"))
//  }

  signPlugin {
    certificateChain = environment("CERTIFICATE_CHAIN")
    privateKey = environment("PRIVATE_KEY")
    password = environment("PRIVATE_KEY_PASSWORD")
  }

  publishPlugin {
    token = environment("PUBLISH_TOKEN")
    channels = listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first())
  }

  runIde {
    ideDir = fileProperties("idePath")
  }

  register("markdownToHtml") {
    val input = File("${project.projectDir}/docs/CHANGELOG.md")
    File("${project.projectDir}/docs/CHANGELOG.html").run {
      writeText(markdownToHTML(input.readText()))
    }
  }
}
