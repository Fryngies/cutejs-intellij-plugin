package org.cutejs.ide.highlight

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import org.cutejs.lang.psi.*

class CuteHighlightVisitor : CuteVisitor(), HighlightVisitor {
    private var infoHolder: HighlightInfoHolder? = null

    override fun visitNamespaceArgs(element: CuteNamespaceArgs) {
        val cuteFile = element.containingFile as CuteFile
        val generatedFile = cuteFile.getOrFindGeneratedFile()

        if (generatedFile != null && generatedFile.namespace == element.node.text) {
            val lastIdentifier = element.expr.lastChild
            highlightDeclaration(lastIdentifier)
        }
    }

    override fun visitExportArgs(element: CuteExportArgs) {
        highlightPropertyIfResolved(element.firstChild)
    }

    override fun visitTypedef(element: CuteTypedef) {
        highlightPropertyIfResolved(element.thisProperty.lastChild)
    }

    override fun visit(element: PsiElement) {
        element.accept(this)
    }

    override fun analyze(file: PsiFile, updateWholeFile: Boolean, holder: HighlightInfoHolder, action: Runnable): Boolean {
        infoHolder = holder
        action.run()

        return true
    }

    override fun visitWhiteSpace(space: PsiWhiteSpace?) {}

    override fun clone(): HighlightVisitor = CuteHighlightVisitor()

    override fun suitableForFile(file: PsiFile): Boolean = file is CuteFile

    override fun order(): Int = 1

    private fun highlightPropertyIfResolved(element: PsiElement) {
        val cuteFile = element.containingFile as CuteFile
        val generatedFile = cuteFile.getOrFindGeneratedFile() ?: return

        if (CuteResolveUtil.findElementsInFile(element.node.text, generatedFile.file) != null) {
            highlightProperty(element)
        }
    }

    private fun highlightProperty(element: PsiElement) {
        val builder = HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION)
        builder.textAttributes(CuteHighlighter.PROPERTY)
        builder.range(element)

        infoHolder?.add(builder.create())
    }

    private fun highlightDeclaration(element: PsiElement) {
        val builder = HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION)
        builder.textAttributes(CuteHighlighter.DECLARATION)
        builder.range(element)

        infoHolder?.add(builder.create())
    }
}