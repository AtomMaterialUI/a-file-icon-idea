/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Elior "Mallowigi" Boukhobza
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

package com.mallowigi.icons.patchers

import com.intellij.openapi.project.ProjectManager
import com.mallowigi.config.select.AtomProjectSelectConfig
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.models.PsiFileInfo

/**
 * Resolves user PSI icon overrides for an already glyph-rewritten path.
 *
 * The glyph patchers first rewrite a platform icon path (e.g. `/nodes/class.svg`) into its glyph variant
 * (e.g. `/glyphs/nodes/class.svg`). This helper is then applied to that glyph path to let project or global PSI
 * associations override the icon. Keeping the composition inside [GlyphIconsPatcher] avoids the internal
 * `IconLoader.installPostPathPatcher` API while preserving the "match on the rewritten glyph path" behavior.
 */
object PsiIconOverrides {

  /**
   * Return the overriding icon path for a glyph path, or `null` when no PSI association matches.
   *
   * Project-level overrides take precedence over global ones.
   *
   * @param glyphPath the glyph path produced by a [GlyphIconsPatcher]
   */
  fun findOverride(glyphPath: String): String? {
    val fileInfo = PsiFileInfo(glyphPath)

    val openProjects = ProjectManager.getInstance().openProjects
    for (project in openProjects) {
      val projectMatch = AtomProjectSelectConfig.getInstance(project).selectedPsiAssociations.findAssociation(fileInfo, true)
      if (projectMatch != null) return projectMatch.icon
    }

    val globalMatch = AtomSelectConfig.instance.selectedPsiAssociations.findAssociation(fileInfo, true)
    return globalMatch?.icon
  }
}
