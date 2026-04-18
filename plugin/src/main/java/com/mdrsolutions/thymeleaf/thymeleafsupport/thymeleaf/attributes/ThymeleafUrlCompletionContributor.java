package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.attributes;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import com.mdrsolutions.thymeleaf.thymeleafsupport.spring.ControllerMappingInfo;
import com.mdrsolutions.thymeleaf.thymeleafsupport.spring.ControllerUrlUtil;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ThymeleafUrlCompletionContributor extends CompletionContributor {
    public ThymeleafUrlCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .withParent(XmlPatterns.xmlAttributeValue()
                                .withParent(XmlPatterns.xmlAttribute()
                                        .withName("th:href", "th:action"))
                        ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        // Get all controller URLs (use your utility below)
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
}

