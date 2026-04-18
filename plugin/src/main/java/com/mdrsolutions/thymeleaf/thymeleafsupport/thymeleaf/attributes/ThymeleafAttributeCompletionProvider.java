package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.attributes;

import com.mdrsolutions.thymeleaf.thymeleafsupport.Thymeleaf;
import com.mdrsolutions.thymeleaf.thymeleafsupport.base.AttributeUtil;
import com.mdrsolutions.thymeleaf.thymeleafsupport.base.BaseAttributeCompletionProvider;
import com.mdrsolutions.thymeleaf.thymeleafsupport.namespace.ThymeleafNamespaceRegistry;
import consulo.html.language.psi.HtmlTag;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.logging.Logger;
import consulo.ui.image.Image;

public class ThymeleafAttributeCompletionProvider extends BaseAttributeCompletionProvider {

    private static final Logger logger = Logger.getInstance(ThymeleafAttributeCompletionProvider.class);

    @Override
    protected void addCompletionsForType(CompletionParameters parameters, CompletionResultSet resultSet, AttributeUtil attributeUtil) {
        ThymeleafAttributeUtil.getInstance().getAttributes().forEach(attribute -> resultSet.addElement(buildLookupElement(attribute, ThymeleafAttributeUtil.getAttributeDescription(attribute))));
    }

    @Override
    protected ThymeleafAutoCompleteSuggestions createSuggestions(HtmlTag tag, String partialAttribute) {
        return new ThymeleafAutoCompleteSuggestions(tag, partialAttribute);
    }

    private LookupElementBuilder buildLookupElement(String attribute, String typeText) {
        return LookupElementBuilder.create(attribute)
                .withCaseSensitivity(false)
                .withIcon(Thymeleaf.ICON)
                .withTypeText(typeText);
    }

    @Override
    protected AttributeUtil getAttributeUtil() {
        return ThymeleafAttributeUtil.getInstance();
    }

    @Override
    protected String getAttributeStartingChars() {
        return THYMELEAF_ATTRIBUTE;
    }

    @Override
    public String getNamespaceAttr() {
        return ThymeleafNamespaceRegistry.NamespaceAttribute.THYMELEAF.getAttribute();
    }

    @Override
    public String getNamespaceValue() {
        return ThymeleafNamespaceRegistry.getNamespaceValue(ThymeleafNamespaceRegistry.NamespaceAttribute.THYMELEAF);
    }

    @Override
    public Image getIcon() {
        return Thymeleaf.ICON;
    }
}
