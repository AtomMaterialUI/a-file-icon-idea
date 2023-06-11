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

package com.mallowigi.utils

import com.dynatrace.hash4j.hashing.HashFunnel
import com.dynatrace.hash4j.hashing.Hashing
import com.intellij.ui.ColorUtil
import java.awt.Color
import java.util.*

operator fun <T> Lazy<T>.getValue(thisRef: Any?, property: Any) = value

/**
 * To optional
 *
 * @param T
 */
fun <T : Any> T?.toOptional(): Optional<T> = Optional.ofNullable(this)


/** Convert a color to a hex string. */
fun Color.toHex(): String = ColorUtil.toHex(this)

/** Get color from hex. */
fun String.fromHex(): Color = ColorUtil.fromHex(this)


fun String.toHash(): Long =
  Hashing.komihash4_3().hashStream().putOrderedIterable(listOf(this), HashFunnel.forString()).asLong

private fun pairWithDigest(list: List<String>): Pair<List<String>, Long> =
  list to Hashing.komihash4_3().hashStream().putOrderedIterable(list, HashFunnel.forString()).asLong
