package com.mdrsolutions.thymeleaf.thymeleafsupport.base;

import consulo.html.language.psi.HtmlTag;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.CompletionUtilCore;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiElement;
import consulo.language.util.ProcessingContext;
import consulo.ui.image.Image;
import consulo.util.lang.StringUtil;
import consulo.xml.codeInsight.completion.XmlAttributeInsertHandler;
import consulo.xml.language.psi.XmlAttribute;
import jakarta.annotation.Nonnull;

public abstract class BaseAttributeCompletionProvider implements CompletionProvider {

    protected static final String LAYOUT_ATTRIBUTE = "layout:";
    protected static final String THYMELEAF_ATTRIBUTE = "th:";
    protected static final String SPRING_SECURITY_ATTRIBUTE = "sec:";

    protected abstract void addCompletionsForType(CompletionParameters parameters,
                                                  CompletionResultSet resultSet,
                                                  AttributeUtil attributeUtil);

    protected abstract String getAttributeStartingChars();

    protected abstract AttributeUtil getAttributeUtil();

    public abstract String getNamespaceAttr();

    public abstract String getNamespaceValue();

    public abstract Image getIcon();

    protected abstract AutoCompleteSuggestionsBase<?> createSuggestions(HtmlTag tag, String partialAttribute);

    @Override
    public void addCompletions(@Nonnull CompletionParameters parameters,
                               @Nonnull ProcessingContext context,
                               @Nonnull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        if (!(position.getParent() instanceof XmlAttribute attribute)) {
            return;
        }

        if (!(attribute.getParent() instanceof HtmlTag xmlTag)) {
            return;
        }

        String partialAttribute = StringUtil.trimEnd(attribute.getName(), CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED);
        if (partialAttribute.isEmpty()) {
            return;
        }

        AutoCompleteSuggestionsBase<?> suggestions = createSuggestions(xmlTag, partialAttribute);

        CompletionResultSet filteredResult = result.withPrefixMatcher(partialAttribute);

        suggestions.getAttributes().forEach(it -> {
            String text = it.getAttribute();

            LookupElementBuilder elementBuilder = LookupElementBuilder.create(text)
                    .withCaseSensitivity(false)
                    .withIcon(getIcon())
                    .withTypeText(it.getDescription())
                    .withInsertHandler((insertionContext, item) -> {
                        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(insertionContext.getProject());
                        psiDocumentManager.commitDocument(insertionContext.getDocument());

                        XmlAttributeInsertHandler.INSTANCE.handleInsert(insertionContext, item);
                        InitiateXMLNamespace.writeNamespace(text, insertionContext);
                    });
            filteredResult.addElement(elementBuilder);
        });
    }
}
