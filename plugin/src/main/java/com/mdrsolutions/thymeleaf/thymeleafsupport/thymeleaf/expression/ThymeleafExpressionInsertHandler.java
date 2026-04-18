package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression;

import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;

import static com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression.ThymeleafExpressionCompletionContributor.FULL_PROP_CHAIN_KEY;

public abstract class ThymeleafExpressionInsertHandler implements InsertHandler<LookupElement> {

    protected abstract String getExpressionPrefix();

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        Editor editor = context.getEditor();
        Document doc = editor.getDocument();
        Project project = editor.getProject();
        if (project == null) return;

        String fullPropChain = item.getUserData(FULL_PROP_CHAIN_KEY);
        if (fullPropChain == null) {
            fullPropChain = item.getLookupString();
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile(context.getFile().getVirtualFile());
        if (psiFile == null) return;

        PsiElement elementAtCaret = psiFile.findElementAt(editor.getCaretModel().getOffset());
        XmlAttributeValue xmlAttributeValue = findParentOfType(elementAtCaret, XmlAttributeValue.class);
        if (xmlAttributeValue == null) return;

        TextRange valueTextRange = xmlAttributeValue.getValueTextRange();
        String currentAttributeValueText = xmlAttributeValue.getValue();
        int caretOffsetInValueText = editor.getCaretModel().getOffset() - valueTextRange.getStartOffset();

        int exprStartRel = currentAttributeValueText.lastIndexOf(getExpressionPrefix(), caretOffsetInValueText);
        if (exprStartRel == -1) return;

        int exprBodyStartRel = exprStartRel + getExpressionPrefix().length();

        String typedContentBeforeDummy;
        int dummyIdentifierRelPos = currentAttributeValueText.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER, exprBodyStartRel);

        if (dummyIdentifierRelPos != -1) {
            typedContentBeforeDummy = currentAttributeValueText.substring(exprBodyStartRel, dummyIdentifierRelPos);
        } else {
            typedContentBeforeDummy = currentAttributeValueText.substring(exprBodyStartRel, caretOffsetInValueText);
        }
        typedContentBeforeDummy = typedContentBeforeDummy.trim();

        int replacementStartRel;
        int lastDotIndexInTypedContent = typedContentBeforeDummy.lastIndexOf('.');

        if (lastDotIndexInTypedContent != -1) {
            replacementStartRel = exprBodyStartRel + lastDotIndexInTypedContent + 1;
        } else {
            replacementStartRel = exprBodyStartRel;
        }

        int finalReplacementStartOffset = valueTextRange.getStartOffset() + replacementStartRel;

        doc.replaceString(finalReplacementStartOffset, context.getTailOffset(), fullPropChain);
        editor.getCaretModel().moveToOffset(finalReplacementStartOffset + fullPropChain.length());
    }

    private static <T> T findParentOfType(PsiElement element, Class<T> clazz) {
        while (element != null && !clazz.isInstance(element)) {
            element = element.getParent();
        }
        return clazz.cast(element);
    }
}
