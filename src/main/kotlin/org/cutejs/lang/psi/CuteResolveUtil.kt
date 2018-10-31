package org.cutejs.lang.psi

import com.intellij.lang.javascript.psi.resolve.JSClassResolver.findElementsByNameIncludingImplicit
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope

class CuteResolveUtil {
    companion object {
        fun findGeneratedFileByNamespace(namespace: String, project: Project): PsiFile? {
            val lastIdentifier = namespace.split(".").last()
            val projectScope = ProjectScope.getProjectScope(project)

            val fileElement = findElementsByNameIncludingImplicit(lastIdentifier, projectScope, false).firstOrNull {
                val fileName = it.containingFile.virtualFile.name
                val isJstGenFile = fileName.endsWith(".jst.js")
                val elementNamespace = it.namespace?.qualifiedName
                val isTargetNamespace = elementNamespace != null && namespace == "$elementNamespace.$lastIdentifier"

                isJstGenFile && isTargetNamespace
            }

            return fileElement?.containingFile
        }

        fun findElementsInFile(identifier: String, file: PsiFile): Array<PsiElement>? {
            val scope = GlobalSearchScope.fileScope(file.project, file.virtualFile)
            val elements = findElementsByNameIncludingImplicit(identifier, scope, false)

            if (elements.isEmpty()) {
                return null
            }

            return elements.toTypedArray()
        }
    }
}