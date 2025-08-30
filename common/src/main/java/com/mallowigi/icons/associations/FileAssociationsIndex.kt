/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.icons.associations

import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.models.IconType
import com.mallowigi.models.VirtualFileInfo
import java.io.DataInput
import java.io.DataOutput

class FileAssociationsIndex : FileBasedIndexExtension<String, RegexAssociation>() {
  private val myIndexer: DataIndexer<String, RegexAssociation, FileContent> = FileAssociationsIndexer()

  private val myValueExternalizer: DataExternalizer<RegexAssociation> = object : DataExternalizer<RegexAssociation> {
    override fun save(out: DataOutput, value: RegexAssociation) {
      out.writeBoolean(value.enabled)
      out.writeInt(value.priority)
      out.writeUTF(value.iconType.name)
      out.writeUTF(value.name)
      out.writeUTF(value.icon)
      out.writeUTF(value.pattern)
      out.writeUTF(value.iconColor ?: Association.DEFAULT_COLOR)
      out.writeUTF(value.folderColor ?: Association.DEFAULT_COLOR)
      out.writeUTF(value.folderIconColor ?: Association.DEFAULT_COLOR)
    }

    override fun read(input: DataInput): RegexAssociation {
      val association = RegexAssociation()
      association.enabled = input.readBoolean()
      association.priority = input.readInt()
      association.iconType = IconType.valueOf(input.readUTF())
      association.name = input.readUTF()
      association.icon = input.readUTF()
      association.pattern = input.readUTF()
      // Normalize deserialized values
      association.iconColor = input.readUTF().takeIf { it != Association.DEFAULT_COLOR }
      association.folderColor = input.readUTF().takeIf { it != Association.DEFAULT_COLOR }
      association.folderIconColor = input.readUTF().takeIf { it != Association.DEFAULT_COLOR }

      return association
    }
  }

  private val myInputFilter = FileBasedIndex.InputFilter { file: VirtualFile ->
    val project = ProjectLocator.getInstance().guessProjectForFile(file) ?: return@InputFilter false
    val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
    file.isInLocalFileSystem && !projectFileIndex.isExcluded(file)
  }

  override fun getName(): ID<String, RegexAssociation> = NAME

  override fun getInputFilter(): FileBasedIndex.InputFilter = myInputFilter

  override fun dependsOnFileContent(): Boolean = true

  override fun getIndexer(): DataIndexer<String, RegexAssociation, FileContent> = myIndexer

  override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

  override fun getValueExternalizer(): DataExternalizer<RegexAssociation> = myValueExternalizer

  override fun getVersion(): Int = VERSION

  override fun indexDirectories(): Boolean = false

  internal class FileAssociationsIndexer : DataIndexer<String, RegexAssociation, FileContent> {
    override fun map(inputData: FileContent): Map<String, RegexAssociation> {
      val file = inputData.file
      val path = file.path
      val fileInfo = VirtualFileInfo(file)
      val isFolder = file.isDirectory

      when {
        // isFolder && !AtomSettingsConfig.instance.isEnabledDirectories -> return emptyMap()
        !isFolder && !AtomSettingsConfig.instance.isEnabledIcons -> return emptyMap()

        // Find association for the given path
        else                                                     -> {
          val fileAssociations = AtomSelectConfig.instance.selectedFileAssociations
          // val folderAssociations = AtomSelectConfig.instance.selectedFolderAssociations

          // Find association for the given path
          val association = when {
            // isFolder -> folderAssociations.findAssociation(fileInfo)
            else -> fileAssociations.findAssociation(fileInfo)
          }

          if (association != null && association is RegexAssociation) {
            return mapOf(path to association)
          }
          return emptyMap()
        }
      }
    }
  }

  companion object {
    val NAME = ID.create<String, RegexAssociation>("com.mallowigi.icons.associations.fileAssociationsIndex")
    const val VERSION = 3

  }
}
