package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.tags;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.patterns.XmlPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class ThymeleafBlockCompletionContributor extends CompletionContributor {
    public ThymeleafBlockCompletionContributor() {
        extend(
                CompletionType.BASIC,
                // Suggest when completing tag names
                XmlPatterns.psiElement().withParent(XmlPatterns.xmlTag()),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
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
}
