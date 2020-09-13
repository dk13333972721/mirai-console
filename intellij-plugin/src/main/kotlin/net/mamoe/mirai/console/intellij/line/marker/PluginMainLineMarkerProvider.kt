package net.mamoe.mirai.console.intellij.line.marker

import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.util.castSafelyTo
import net.mamoe.mirai.console.intellij.Icons
import net.mamoe.mirai.console.intellij.Plugin_FQ_NAME
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtReferenceExpression

class PluginMainLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is KtReferenceExpression) return null
        val objectDeclaration =
            element.parents.filterIsInstance<KtObjectDeclaration>().firstOrNull() ?: return null
        val kotlinPluginClass =
            element.resolve().castSafelyTo<KtConstructor<*>>()?.parent?.castSafelyTo<KtClass>() ?: return null
        if (kotlinPluginClass.allSuperNames.none { it == Plugin_FQ_NAME }) return null
        return Info(getElementForLineMark(objectDeclaration))
    }

    @Suppress("DEPRECATION")
    class Info(
        callElement: PsiElement,
    ) : LineMarkerInfo<PsiElement>(
        callElement,
        callElement.textRange,
        Icons.PluginMainDeclaration,
        Pass.LINE_MARKERS,
        {
            "Mirai Console Plugin"
        },
        null,
        GutterIconRenderer.Alignment.RIGHT
    ) {
        override fun createGutterRenderer(): GutterIconRenderer? {
            return object : LineMarkerInfo.LineMarkerGutterIconRenderer<PsiElement>(this) {
                override fun getClickAction(): AnAction? = null
            }
        }
    }
}