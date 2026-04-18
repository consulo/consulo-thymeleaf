package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.attributes;

import com.mdrsolutions.thymeleaf.thymeleafsupport.base.AutoCompleteSuggestionsBase;
import consulo.html.language.psi.HtmlTag;
import consulo.logging.Logger;

public class ThymeleafAutoCompleteSuggestions extends AutoCompleteSuggestionsBase<ThymeleafAttributeInfo> {

    private static final Logger logger = Logger.getInstance(ThymeleafAutoCompleteSuggestions.class);

    private ThymeleafAttributeUtil thymeleafAttributeUtil;

    public ThymeleafAutoCompleteSuggestions(HtmlTag htmlTag, String partialAttribute) {
        super(htmlTag, partialAttribute);
        this.thymeleafAttributeUtil = ThymeleafAttributeUtil.getInstance();
        addAttributes();
    }

    @Override
    protected void addAttributes() {
        if (thymeleafAttributeUtil == null) {
            thymeleafAttributeUtil = ThymeleafAttributeUtil.getInstance();
        }
        logger.info("size of attributes added is " + thymeleafAttributeUtil.getAttributes().size());
        for (String attribute : thymeleafAttributeUtil.getAttributes()) {
            if (attribute.startsWith(partialAttribute)) {
                attributes.add(new ThymeleafAttributeInfo(attribute));
            }
        }
    }
}
