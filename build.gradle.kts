/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 Elior "Mallowigi" Boukhobza
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
 */
@file:Suppress("SpellCheckingInspection", "HardCodedStringLiteral")

import io.gitlab.arturbosch.detekt.Detekt
import kotlinx.coroutines.runBlocking
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.HttpURLConnection
import java.nio.file.Paths
import java.util.*

fun properties(key: String) = providers.gradleProperty(key).get()
fun environment(key: String) = providers.environmentVariable(key)
fun fileContents(filePath: String) = providers.fileContents(layout.projectDirectory.file(filePath)).asText

val pluginsVersion: String by project
val rustVersion: String by project

val platformType: String by project
val platformVersion: String by project

val pluginName: String by project
val pluginID: String by project
val pluginVersion: String by project
val pluginDescription: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project

val pluginCode: String by project
val pluginReleaseDate: String by project
val pluginReleaseVersion: String by project

val pluginVendorName: String by project
val pluginVendorEmail: String by project
val pluginVendorUrl: String by project

val pluginChannels: String by project

val javaVersion: String by project
val gradleVersion: String by project

group = pluginID
version = pluginsVersion

plugins {
  id("java")
  alias(libs.plugins.kotlin)
  alias(libs.plugins.gradleIntelliJPlugin)
  alias(libs.plugins.changelog)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
}

dependencies {
  intellijPlatform {
    intellijIdeaUltimate(platformVersion, useInstaller = false)
    instrumentationTools()
    //    local(properties("idePath").get())

    pluginVerifier()
    zipSigner()

//    jetbrainsRuntime("21")

    bundledPlugins(
      "com.intellij.java",
      "Git4Idea",
    )
  }

  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
  implementation("com.jgoodies:jgoodies-forms:1.9.0")
  implementation("com.thoughtworks.xstream:xstream:1.4.20")
  implementation("org.javassist:javassist:3.30.2-GA")
  implementation(project(":common"))
  runtimeOnly(project(":rider"))
}

allprojects {
  apply {
    plugin("java")
    plugin("org.jetbrains.kotlin.jvm")
    plugin("org.jetbrains.intellij.platform")
  }

  intellijPlatform {
    buildSearchableOptions = false
    instrumentCode = true
  }


  repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()

    intellijPlatform {
      defaultRepositories()

      // marketplace()
      // localPlatformArtifacts()
    }

    intellijPlatform {
      defaultRepositories()

      marketplace()
      // localPlatformArtifacts()
    }
  }

  tasks {
    javaVersion.let {
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

  }
}

intellijPlatform {
  buildSearchableOptions = false
  instrumentCode = true

  projectName = pluginName

  pluginConfiguration {
    id = pluginID
    name = pluginName
    version = pluginVersion
    description = pluginDescription

    //Get the latest available change notes from the changelog file
    val changelog = project.changelog // local variable for configuration cache compatibility
    // Get the latest available change notes from the changelog file
    val pluginVersion = pluginVersion
    changeNotes.set(provider {
      with(changelog) {
        renderItem(
          (getOrNull(pluginVersion) ?: getUnreleased())
            .withHeader(false)
            .withEmptySections(false),
          Changelog.OutputType.HTML,
        )
      }
    })

    ideaVersion {
      sinceBuild = pluginSinceBuild
      untilBuild = pluginUntilBuild
    }

    vendor {
      name = pluginVendorName
      email = pluginVendorEmail
      url = pluginVendorUrl
    }
  }

  publishing {
    token = environment("INTELLIJ_PUBLISH_TOKEN")
    channels = pluginChannels.split(',').map { it.trim() }
  }

  signing {
    certificateChain = fileContents("./chain.crt")
    privateKey = fileContents("./private.pem")
    password = fileContents("./private_encrypted.pem")
  }

  pluginVerification {
    ides {
      recommended()
      select {
        sinceBuild = pluginSinceBuild
        untilBuild = pluginUntilBuild
      }
    }
  }
}

changelog {
  path.set("${project.projectDir}/docs/CHANGELOG.md")
  version.set(pluginVersion)
  header.set(provider { version.get() })
  headerParserRegex.set("(\\d+\\.\\d+\\.\\d+)")
  itemPrefix.set("-")
  keepUnreleasedSection.set(true)
  unreleasedTerm.set("Changelog")
  groups.set(listOf("Features", "Fixes", "Removals", "Additions", "Other"))
}

detekt {
  config.from(files("./detekt-config.yml"))
  buildUponDefaultConfig = true
  autoCorrect = true
  ignoreFailures = true
}

tasks {
  wrapper {
    gradleVersion = properties("gradleVersion")
  }

  withType<Detekt> {
    jvmTarget = javaVersion
    reports.xml.required.set(true)
  }

  register("markdownToHtml") {
    val input = File("${project.projectDir}/docs/CHANGELOG.md")
    File("${project.projectDir}/docs/CHANGELOG.html").run {
      writeText(markdownToHTML(input.readText()))
    }
  }

  register("fetchPluginVersion") {
    doLast {
      runBlocking {
        fetchPluginVersion("PythonCore", "pluginsVersion")
      }
    }
  }
}


fun fetchPluginVersion(id: String, property: String) {
  val url = Paths.get("https://plugins.jetbrains.com/plugins/list?pluginId=$id").toUri().toURL()
  val connection = url.openConnection() as HttpURLConnection
  connection.requestMethod = "GET"

  if (connection.responseCode == 200) {
    val xml = connection.inputStream.bufferedReader().use { it.readText() }
    val version = parseXml(xml)

    if (version != null) {
      updateGradleProperties(property, version)
    }

  } else {
    println("Failed to fetch XML: ${connection.responseCode}")
  }
}

fun parseXml(xml: String): String? {
  val document: Document = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser())
  val version = document.select("plugin-repository > category > idea-plugin").first()?.select("version")?.text()
  println("Version: $version")
  return version
}

fun updateGradleProperties(propertyName: String, propertyValue: String) {
  val propertiesFile = File("gradle.properties")
  val properties = Properties()

  if (propertiesFile.exists()) {
    propertiesFile.inputStream().use { properties.load(it) }
  }

  properties[propertyName] = propertyValue
  propertiesFile.outputStream().use { properties.store(it, null) }
  println("Updated $propertyName in gradle.properties to $propertyValue")
}
