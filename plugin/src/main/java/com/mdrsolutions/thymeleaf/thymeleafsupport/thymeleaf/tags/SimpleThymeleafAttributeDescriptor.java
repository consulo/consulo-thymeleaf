package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.tags;

import consulo.language.psi.PsiElement;
import consulo.xml.descriptor.XmlAttributeDescriptor;
import consulo.xml.language.psi.XmlElement;
import jakarta.annotation.Nullable;

public class SimpleThymeleafAttributeDescriptor implements XmlAttributeDescriptor {
    private final String name;

    public SimpleThymeleafAttributeDescriptor(String name) {
        this.name = name;
    }

    @Override
    public PsiElement getDeclaration() {
        return null;
    }

    @Override
    public String getName(PsiElement context) {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init(PsiElement element) {
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public boolean hasIdType() {
        return false;
    }

    @Override
    public boolean hasIdRefType() {
        return false;
    }

    @Override
    public String getDefaultValue() {
        return null;
    }

    @Override
    public boolean isEnumerated() {
        return false;
    }

    @Override
    public String[] getEnumeratedValues() {
        return new String[0];
    }

    @Override
    @Nullable
    public String validateValue(XmlElement context, String value) {
        return null;
    }

    @Override
    public Object[] getDependences() {
        return new Object[0];
    }
}
