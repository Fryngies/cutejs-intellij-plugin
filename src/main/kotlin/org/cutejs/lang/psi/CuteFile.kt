package org.cutejs.lang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

import org.cutejs.lang.CuteLanguage
import org.cutejs.lang.CuteFileType
import org.cutejs.lang.psi.impl.CuteStatementImpl

class CuteFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CuteLanguage.INSTANCE) {
    private var generatedFile: CuteGeneratedFile? = null

    fun getOrFindGeneratedFile(): CuteGeneratedFile? {
        if (generatedFile == null || generatedFile?.file?.virtualFile?.exists() == false) {
            generatedFile = findGeneratedFile()
        }

        return generatedFile
    }

    override fun getFileType(): FileType {
        return CuteFileType.INSTANCE
    }

    override fun toString(): String {
        return "CuteJSFile:${this.name}"
    }

    fun templateNamespaceIdentifier(): CuteNamespaceArgs? {
        val statement = PsiTreeUtil.getChildOfType(this, CuteStatementImpl::class.java)
        val namespace = statement?.expression?.namespace

        return namespace?.namespaceArgs
    }

    private fun findGeneratedFile(): CuteGeneratedFile? {
        val templateNamespace = templateNamespaceIdentifier()?.text ?: return null
        return CuteResolveUtil.findGeneratedFileByNamespace(templateNamespace, project)
    }
}