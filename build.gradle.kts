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
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String): Provider<String> = providers.gradleProperty(key)
fun environment(key: String): Provider<String> = providers.environmentVariable(key)
fun fileContents(filePath: String): Provider<String> = providers.fileContents(layout.projectDirectory.file(filePath)).asText

plugins {
  id("java")
  alias(libs.plugins.kotlin)
  alias(libs.plugins.gradleIntelliJPlugin)
  alias(libs.plugins.changelog)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
}

group = properties("pluginID").get()
version = properties("pluginVersion").get()

dependencies {
  intellijPlatform {
    intellijIdeaUltimate(properties("platformVersion").get())
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

  repositories {
    mavenCentral()

    intellijPlatform {
      defaultRepositories()

      marketplace()
      // localPlatformArtifacts()
    }
  }

  java {
    toolchain {
      languageVersion = JavaLanguageVersion.of(17)
    }
  }

  kotlin {
    jvmToolchain(17)
  }

  intellijPlatform {
    buildSearchableOptions = false
    instrumentCode = true
  }

  tasks {
    properties("javaVersion").get().let {
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

    withType<Detekt> {
      jvmTarget = properties("javaVersion").get()
      reports.xml.required.set(true)
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

  projectName = properties("pluginName").get()

  pluginConfiguration {
    id = properties("pluginID").get()
    name = properties("pluginName").get()
    version = properties("pluginVersion").get()
//    description.set(properties("pluginDescription").get())

    // Get the latest available change notes from the changelog file
    val changelog = project.changelog // local variable for configuration cache compatibility
    // Get the latest available change notes from the changelog file
    val pluginVersion = properties("pluginVersion").get()
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
      sinceBuild = properties("pluginSinceBuild").get()
      untilBuild = properties("pluginUntilBuild").get()
    }

    vendor {
      name = properties("pluginVendorName").get()
      email = properties("pluginVendorEmail").get()
      url = properties("pluginVendorUrl").get()
    }
  }

  publishing {
    token = environment("INTELLIJ_PUBLISH_TOKEN")
    channels = properties("pluginChannels").get().split(',').map { it.trim() }
//    hidden = properties("pluginHidden").get().toBoolean()
  }

  signing {
    certificateChain = fileContents("./chain.crt")
    privateKey = fileContents("./private.pem")
    password = fileContents("./private_encrypted.pem")
  }
}

changelog {
  path.set("${project.projectDir}/docs/CHANGELOG.md")
  version.set(properties("pluginVersion").get())
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
    gradleVersion = properties("gradleVersion").get()
  }

  withType<Detekt> {
    jvmTarget = properties("javaVersion").get()
    reports.xml.required.set(true)
  }

  register("markdownToHtml") {
    val input = File("${project.projectDir}/docs/CHANGELOG.md")
    File("${project.projectDir}/docs/CHANGELOG.html").run {
      writeText(markdownToHTML(input.readText()))
    }
  }
}
