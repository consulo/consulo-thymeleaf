package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.attributes;

import consulo.annotation.component.ExtensionImpl;
import consulo.html.language.HTMLLanguage;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionType;
import consulo.language.pattern.PlatformPatterns;
import consulo.logging.Logger;
import consulo.xml.language.psi.pattern.XmlPatterns;
import consulo.xml.language.psi.XmlTokenType;
import jakarta.annotation.Nonnull;

@ExtensionImpl
public class ThymeleafCompletionContributor extends CompletionContributor {
    private static final Logger logger = Logger.getInstance(ThymeleafCompletionContributor.class);

    public ThymeleafCompletionContributor() {
        logger.info("ThymeleafCompletionContributor loaded");
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(XmlTokenType.XML_NAME)
                        .withParent(XmlPatterns.xmlAttribute()),
                new ThymeleafAttributeCompletionProvider()
        );
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return HTMLLanguage.INSTANCE;
    }
}
