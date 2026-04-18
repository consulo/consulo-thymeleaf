package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression;

import consulo.codeEditor.Editor;
import consulo.document.Document;
import consulo.document.util.TextRange;
import consulo.language.editor.completion.CompletionUtilCore;
import consulo.language.editor.completion.lookup.InsertHandler;
import consulo.language.editor.completion.lookup.InsertionContext;
import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.project.Project;
import consulo.xml.language.psi.XmlAttributeValue;
import jakarta.annotation.Nonnull;

import static com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression.ThymeleafExpressionCompletionContributor.FULL_PROP_CHAIN_KEY;

public abstract class ThymeleafExpressionInsertHandler implements InsertHandler<LookupElement> {

    protected abstract String getExpressionPrefix();

    @Override
    public void handleInsert(@Nonnull InsertionContext context, @Nonnull LookupElement item) {
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
