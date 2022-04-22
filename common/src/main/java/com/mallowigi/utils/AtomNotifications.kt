/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.notification.impl.NotificationsManagerImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.BalloonLayoutData
import com.intellij.ui.awt.RelativePoint
import com.mallowigi.config.AtomSettingsBundle.message
import org.jetbrains.annotations.NotNull
import java.awt.Point
import java.util.Objects

/**
 * Atom notifications support
 *
 * @constructor Create empty Atom notifications
 */
object AtomNotifications {
  /** Notification channel. */
  private const val CHANNEL: String = "Atom Material Notifications"

  /**
   * Show the update notification
   *
   * @param project the project to display in
   */
  @Suppress("DialogTitleCapitalization")
  @JvmStatic
  fun showUpdate(project: Project) {
    val notification = createNotification(
      message("notification.update.title", getVersion()),
      message("notification.update.content"),
      NotificationType.INFORMATION
    )
    showFullNotification(project, notification)
  }

  /**
   * Show simple notification
   *
   * @param project
   * @param content
   */
  @Suppress("UnstableApiUsage")
  @JvmStatic
  fun showSimple(project: Project, @NlsContexts.NotificationContent content: String) {
    val notification = createNotification("", content, NotificationType.INFORMATION)
    Notifications.Bus.notify(notification, project)
  }

  /**
   * Shows [Notification] in [AtomNotifications.CHANNEL] group.
   *
   * @param project current project
   * @param title notification title
   * @param content notification text
   * @param type notification type
   * @param action action to run on link click
   */
  @Suppress("UnstableApiUsage")
  @JvmStatic
  fun showWithListener(
    project: Project,
    @NlsContexts.NotificationTitle title: String,
    @NlsContexts.NotificationContent content: String,
    type: NotificationType,
    action: AnAction,
  ) {
    val notification = createNotification(title, content, type, action)
    Notifications.Bus.notify(notification, project)
  }

  /**
   * Create a notification
   *
   * @param title notification title
   * @param content the content
   * @param type the type (sticky...)
   * @return new notification to be displayed
   */
  @Suppress("UnstableApiUsage")
  private fun createNotification(
    @NlsContexts.NotificationTitle title: String,
    @NlsContexts.NotificationContent content: String,
    type: NotificationType,
  ): Notification {
    val group = NotificationGroupManager.getInstance().getNotificationGroup(CHANNEL)
    return group.createNotification(title, content, type)
  }

  /**
   * Create a notification
   *
   * @param title notification title
   * @param content the content
   * @param type the type (sticky...)
   * @param action action to run on link click
   * @return new notification to be displayed
   */
  @Suppress("UnstableApiUsage")
  private fun createNotification(
    @NlsContexts.NotificationTitle title: String,
    @NlsContexts.NotificationContent content: String,
    type: NotificationType,
    @NotNull action: AnAction,
  ): Notification = createNotification(title, content, type).addAction(action)

  /**
   * Show a notification using the Balloon API instead of the bus Credit
   * to @vladsch
   *
   * @param project the project to display into
   * @param notification the notification to display
   */
  private fun showFullNotification(project: Project, notification: Notification) {
    run {
      val frame = WindowManager.getInstance().getIdeFrame(project) ?: return
      val bounds = Objects.requireNonNull(frame).component.bounds

      @Suppress("MagicNumber")
      val target = RelativePoint(frame.component, Point(bounds.x + bounds.width, 20))
      try {
        // Create a notification balloon using the manager
        val balloon = NotificationsManagerImpl.createBalloon(
          frame,
          notification,
          true,
          true,
          BalloonLayoutData.fullContent(),
          project
        )
        // Display the balloon at the top right
        balloon.show(target, Balloon.Position.atLeft)
      } catch (e: NoSuchMethodError) {
        notification.notify(project)
      } catch (e: NoClassDefFoundError) {
        notification.notify(project)
      } catch (e: NoSuchFieldError) {
        notification.notify(project)
      }
    }
  }
}
