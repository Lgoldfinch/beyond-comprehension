package org.jetbrains.scala.samples

import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.{PsiDocumentManager, PsiElement}
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.scala.extensions.PsiElementExt
import org.jetbrains.plugins.scala.lang.psi.api.expr.{ScExpression, ScFor}
import org.jetbrains.plugins.scala.lang.psi.api.statements.ScValue
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaPsiElementFactory

class ForComprehensionGenerator extends CodeInsightAction {
  override def actionPerformedImpl(project: Project, editor: Editor): Unit = {
    val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument)
    val selectedElement = psiFile.findElementAt(editor.getCaretModel.getOffset)
    val enclosingFor = PsiTreeUtil.getParentOfType(selectedElement, classOf[ScFor], false)

    if (enclosingFor == null) {
      val enclosingValue = PsiTreeUtil.getParentOfType(selectedElement, classOf[ScValue], false)

      if (enclosingValue != null) {
        val expr = enclosingValue.expr.orNull

        if (expr != null) {
          val forText = s"for { x <- $expr } yield x"
          val forExpr = ScalaPsiElementFactory.createExpressionFromText(forText, psiFile)
          enclosingValue.replace(forExpr)
        }
      }
    }
  }
}
