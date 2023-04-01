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

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts

/** Atom notifications support. */
object AtomNotifications {

  /** Notification channel. */
  private const val CHANNEL: String = "Atom Material Notifications"

  /**
   * Show simple notification
   *
   * @param project
   * @param content
   */
  @Suppress("UnstableApiUsage")
  @JvmStatic
  fun showSimple(project: Project, @NlsContexts.NotificationContent content: String) {
    val notification = createNotification(content)
    Notifications.Bus.notify(notification, project)
  }

  /**
   * Create a notification
   *
   * @param content the content
   * @return new notification to be displayed
   */
  @Suppress("UnstableApiUsage")
  private fun createNotification(@NlsContexts.NotificationContent content: String): Notification {
    val group = NotificationGroupManager.getInstance().getNotificationGroup(CHANNEL)
    return group.createNotification("", content, NotificationType.INFORMATION)
  }

}
