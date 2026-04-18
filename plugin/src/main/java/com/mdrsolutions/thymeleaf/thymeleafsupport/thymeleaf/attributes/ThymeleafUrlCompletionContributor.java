package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.attributes;

import com.mdrsolutions.thymeleaf.thymeleafsupport.spring.ControllerMappingInfo;
import com.mdrsolutions.thymeleaf.thymeleafsupport.spring.ControllerUrlUtil;
import consulo.annotation.component.ExtensionImpl;
import consulo.html.language.HTMLLanguage;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.CompletionType;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.pattern.PlatformPatterns;
import consulo.language.util.ProcessingContext;
import consulo.xml.language.psi.pattern.XmlPatterns;
import jakarta.annotation.Nonnull;

import java.util.List;

@ExtensionImpl
public class ThymeleafUrlCompletionContributor extends CompletionContributor {
    public ThymeleafUrlCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .withParent(XmlPatterns.xmlAttributeValue()
                                .withParent(XmlPatterns.xmlAttribute()
                                        .withName("th:href", "th:action"))
                        ),
                new CompletionProvider() {
                    @Override
                    public void addCompletions(@Nonnull CompletionParameters parameters,
                                               @Nonnull ProcessingContext context,
                                               @Nonnull CompletionResultSet result) {
                        List<ControllerMappingInfo> mappings = ControllerUrlUtil.getControllerUrls(parameters.getPosition().getProject());
                        for (ControllerMappingInfo info : mappings) {
                            result.addElement(
                                    LookupElementBuilder.create("@{" + info.url + "}")
                                            .withTypeText(info.method + " " + info.controllerName + "#" + info.methodName, true)
                                            .withTailText("  Spring mapping", true)
                            );
                        }
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
