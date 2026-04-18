package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.tags;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.AllIcons;
import consulo.html.language.HTMLLanguage;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.CompletionType;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.util.ProcessingContext;
import consulo.xml.language.psi.pattern.XmlPatterns;
import jakarta.annotation.Nonnull;

@ExtensionImpl
public class ThymeleafBlockCompletionContributor extends CompletionContributor {
    public ThymeleafBlockCompletionContributor() {
        extend(
                CompletionType.BASIC,
                XmlPatterns.psiElement().withParent(XmlPatterns.xmlTag()),
                new CompletionProvider() {
                    @Override
                    public void addCompletions(@Nonnull CompletionParameters parameters,
                                               @Nonnull ProcessingContext context,
                                               @Nonnull CompletionResultSet result) {
                        result.addElement(
                                LookupElementBuilder.create("th:block")
                                        .withIcon(AllIcons.Nodes.Tag)
                                        .withTypeText("Thymeleaf Synthetic th:block tag", true)
                                        .withTailText(" — only element processor (not an attribute). Groups content for processing; Thymeleaf will execute these attributes and then simply make the block, but not its contents, disappear", true)
                                        .withBoldness(true)
                        );
                    }
                }
        );
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return HTMLLanguage.INSTANCE;
    }
}
