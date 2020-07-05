package com.mallowigi.utils

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager


fun refresh(project: Project?) {
  if (project != null) {
    val view = ProjectView.getInstance(project)
    view.refresh()
    if (view.currentProjectViewPane != null) {
      view.currentProjectViewPane.updateFromRoot(true)
    }
  }
}

fun refreshOpenedProjects() {
  val projects: Array<Project> = ProjectManager.getInstance().openProjects
  for (project in projects) {
    refresh(project)
  }
}
