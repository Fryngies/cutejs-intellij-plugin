package org.cutejs.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import org.cutejs.lang.CuteFileType


class CuteElementFactory {
    companion object {
        fun createNamespace(project: Project, namespace: String): CuteNamespace {
            val file = createFile(project, "{{$$namespace}}")
            return file.templateNamespace()!!
        }

        fun createFile(project: Project, text: String): CuteFile {
            val name = "dummy.cute"
            val file = PsiFileFactory
                    .getInstance(project)
                    .createFileFromText(name, CuteFileType.INSTANCE, text)
            return file as CuteFile
        }
    }
}