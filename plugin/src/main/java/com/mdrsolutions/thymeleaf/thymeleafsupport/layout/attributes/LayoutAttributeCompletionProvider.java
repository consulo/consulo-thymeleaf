package com.mdrsolutions.thymeleaf.thymeleafsupport.layout.attributes;

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

public class LayoutAttributeCompletionProvider extends BaseAttributeCompletionProvider {

    private static final Logger logger = Logger.getInstance(LayoutAttributeCompletionProvider.class);

    @Override
    protected void addCompletionsForType(CompletionParameters parameters, CompletionResultSet resultSet, AttributeUtil attributeUtil) {
        LayoutAttributeUtil.getInstance().getAttributes().forEach(attribute -> resultSet.addElement(buildLookupElement(attribute, LayoutAttributeUtil.getAttributeDescription(attribute))));
    }

    private LookupElementBuilder buildLookupElement(String attribute, String typeText) {
        return LookupElementBuilder.create(attribute)
                .withCaseSensitivity(false)
                .withIcon(Thymeleaf.ICON)
                .withTypeText(typeText);
    }

    @Override
    protected String getAttributeStartingChars() {
        return LAYOUT_ATTRIBUTE;
    }

    @Override
    protected AttributeUtil getAttributeUtil() {
        return LayoutAttributeUtil.getInstance();
    }

    @Override
    public String getNamespaceAttr() {
        return ThymeleafNamespaceRegistry.NamespaceAttribute.LAYOUT.getAttribute();
    }

    @Override
    public String getNamespaceValue() {
        return ThymeleafNamespaceRegistry.getNamespaceValue(ThymeleafNamespaceRegistry.NamespaceAttribute.LAYOUT);
    }

    @Override
    public Image getIcon() {
        return Thymeleaf.ICON;
    }

    @Override
    protected LayoutAutoCompleteSuggestions createSuggestions(HtmlTag tag, String partialAttribute) {
        return new LayoutAutoCompleteSuggestions(tag, partialAttribute);
    }
}
