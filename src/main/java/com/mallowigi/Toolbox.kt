package com.mallowigi

import com.intellij.ui.ColorUtil
import java.awt.Color
import java.util.*
import java.util.concurrent.Callable
import java.util.stream.Stream

fun <T> getSafely(callable: Callable<T>): Optional<T> =
  try {
    callable.call().toOptional()
  }
  catch (e: Throwable) {
    Optional.empty()
  }

fun runSafely(runner: Runner): Unit =
  try {
    runner.run()
  }
  catch (e: Throwable) {
  }

fun <T> T?.toOptional() = Optional.ofNullable(this)

fun <T> T?.toStream(): Stream<T> = Stream.of(this)

fun <T> Optional<T>.doOrElse(present: (T) -> Unit, notThere: () -> Unit) =
  this.map {
    it to true
  }.map {
    it.toOptional()
  }.orElseGet {
    (null to false).toOptional()
  }.ifPresent {
    if (it.second) {
      present(it.first)
    }
    else {
      notThere()
    }
  }

interface Runner {
  fun run()
}

fun Color.toHexString() = "#${ColorUtil.toHex(this)}"

fun String.toColor() = ColorUtil.fromHex(this)