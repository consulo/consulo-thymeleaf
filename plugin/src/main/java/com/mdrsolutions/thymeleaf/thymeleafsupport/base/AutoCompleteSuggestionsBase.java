package com.mdrsolutions.thymeleaf.thymeleafsupport.base;

import consulo.html.language.psi.HtmlTag;

import java.util.ArrayList;
import java.util.List;

public abstract class AutoCompleteSuggestionsBase<T extends BaseAttributeInfo> {
    protected final HtmlTag htmlTag;
    protected final String partialAttribute;
    protected final List<T> attributes = new ArrayList<>();

    public AutoCompleteSuggestionsBase(HtmlTag htmlTag, String partialAttribute) {
        this.htmlTag = htmlTag;
        this.partialAttribute = partialAttribute;
        addAttributes();
    }

    public List<T> getAttributes() {
        return attributes;
    }

    protected abstract void addAttributes();
}
