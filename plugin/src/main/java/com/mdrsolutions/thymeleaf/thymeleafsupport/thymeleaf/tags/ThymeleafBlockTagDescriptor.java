package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.tags;

import consulo.language.psi.PsiElement;
import consulo.logging.Logger;
import consulo.xml.descriptor.XmlAttributeDescriptor;
import consulo.xml.descriptor.XmlElementDescriptor;
import consulo.xml.descriptor.XmlElementsGroup;
import consulo.xml.descriptor.XmlNSDescriptor;
import consulo.xml.language.psi.XmlAttribute;
import consulo.xml.language.psi.XmlTag;
import jakarta.annotation.Nullable;

public class ThymeleafBlockTagDescriptor implements XmlElementDescriptor {

    private static final Logger logger = Logger.getInstance(ThymeleafBlockTagDescriptor.class);

    private XmlTag tag;

    @Override
    public String getName() {
        return "th:block";
    }

    @Override
    public void init(PsiElement element) {
        if (element instanceof XmlTag) {
            this.tag = (XmlTag) element;
        }
    }

    @Override
    public String getQualifiedName() {
        return "th:block";
    }

    @Override
    public String getDefaultName() {
        return "th:block";
    }

    @Override
    public XmlTag getDeclaration() {
        return tag;
    }

    @Override
    public String getName(PsiElement context) {
        return "th:block";
    }

    @Override
    public XmlElementDescriptor[] getElementsDescriptors(XmlTag context) {
        return new XmlElementDescriptor[0];
    }

    @Override
    public XmlElementDescriptor getElementDescriptor(XmlTag childTag, XmlTag contextTag) {
        return null;
    }

    @Override
    public XmlNSDescriptor getNSDescriptor() {
        return null;
    }

    @Override
    public int getContentType() {
        return XmlElementDescriptor.CONTENT_TYPE_ANY;
    }

    @Override
    @Nullable
    public String getDefaultValue() {
        return "";
    }

    @Override
    @Nullable
    public XmlElementsGroup getTopGroup() {
        return null;
    }

    @Override
    public XmlAttributeDescriptor[] getAttributesDescriptors(XmlTag context) {
        return new XmlAttributeDescriptor[0];
    }

    @Override
    @Nullable
    public XmlAttributeDescriptor getAttributeDescriptor(String attributeName, @Nullable XmlTag context) {
        logger.info("getAttributeDescriptor called for " + attributeName + " on <" + (context == null ? "null" : context.getName()) + ">");
        if (attributeName != null && attributeName.startsWith("th:")) {
            logger.info("returning new SimpleThymeleafAttributeDescriptor");
            return new SimpleThymeleafAttributeDescriptor(attributeName);
        }
        return null;
    }

    @Override
    @Nullable
    public XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute attribute) {
        String name = attribute.getName();
        logger.info("Attempting to capture XmlAttribute: " + name);
        if (name.startsWith("th:") || name.startsWith("sec:") || name.startsWith("layout:")) {
            logger.info("returning new SimpleThymeleafAttributeDescriptor for XmlAttribute");
            return new SimpleThymeleafAttributeDescriptor(name);
        }
        return null;
    }

    @Override
    public Object[] getDependences() {
        return new Object[0];
    }
}
