package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.tags;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlElementsGroup;
import com.intellij.xml.XmlNSDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

public class ThymeleafBlockTagDescriptor implements XmlElementDescriptor {

    private static final Logger logger = Logger.getInstance(ThymeleafBlockTagDescriptor.class);

    private XmlTag tag;

    @Override
    public String getName() {
        return "th:block";
    }

    @Override
    public void init(PsiElement element) {
        /* doing this to attempt to satisfy Intellij's declaration lookup */
        if(element instanceof XmlTag){
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
    public @NonNls String getName(PsiElement context) {
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
    public @Nullable String getDefaultValue() {
        return "";
    }

    @Override
    public @Nullable XmlElementsGroup getTopGroup() {
        return null;
    }

    @Override
    public XmlAttributeDescriptor[] getAttributesDescriptors(XmlTag context) {
        return new XmlAttributeDescriptor[0];
    }

    @Override
    public @Nullable XmlAttributeDescriptor getAttributeDescriptor(@NonNls String attributeName, @Nullable XmlTag context) {
        logger.info("getAttributeDescriptor called for " + attributeName + " on <" + context.getName() + ">");
        if (attributeName != null && attributeName.startsWith("th:")) {
            logger.info("returning new SimpleThymeleafAttributeDescriptor");
            return new SimpleThymeleafAttributeDescriptor(attributeName);
        }
        return null;
    }

    @Override
    public @Nullable XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute attribute) {
        String name = attribute.getName();
        logger.info("Attempting to capture XmlAttribute: " + name);
        if (name.startsWith("th:") || name.startsWith("sec:") || name.startsWith("layout:")) {
            logger.info("returning new SimpleThymeleafAttributeDescriptor for XmlAttribute");
            return new SimpleThymeleafAttributeDescriptor(name);
        }
        return null;
    }


}
