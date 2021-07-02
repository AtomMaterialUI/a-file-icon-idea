/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Elior "Mallowigi" Boukhobza
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
 *
 */

package com.mallowigi.utils

import com.intellij.ui.ColorUtil
import java.awt.Color
import java.util.Optional
import java.util.concurrent.Callable
import java.util.stream.Stream

/**
 * Get safely
 *
 * @param T
 * @param callable
 * @return
 */
fun <T> getSafely(callable: Callable<T>): Optional<T> =
  try {
    callable.call().toOptional()
  } catch (e: Throwable) {
    Optional.empty()
  }

/**
 * Run safely
 *
 * @param runner
 */
fun runSafely(runner: Runner): Unit =
  try {
    runner.run()
  } catch (e: Throwable) {
    e.printStackTrace()
  }

/**
 * To optional
 *
 * @param T
 */
fun <T> T?.toOptional() = Optional.ofNullable(this)

/**
 * To stream
 *
 * @param T
 * @return
 */
fun <T> T?.toStream(): Stream<T> = Stream.of(this)

/**
 * Do or else
 *
 * @param T
 * @param present
 * @param notThere
 * @receiver
 * @receiver
 */
fun <T> Optional<T>.doOrElse(present: (T) -> Unit, notThere: () -> Unit): Unit =
  this.map {
    it to true
  }.map {
    it.toOptional()
  }.orElseGet {
    (null to false).toOptional()
  }.ifPresent {
    if (it.second) {
      present(it.first)
    } else {
      notThere()
    }
  }

/**
 * Runner
 *
 * @constructor Create empty Runner
 */
interface Runner {
  /**
   * Run
   *
   */
  fun run()
}

/**
 * To hex string
 *
 */
fun Color.toHexString(): String = "#${ColorUtil.toHex(this)}"

/**
 * To color
 *
 */
fun String.toColor(): Color = ColorUtil.fromHex(this)
