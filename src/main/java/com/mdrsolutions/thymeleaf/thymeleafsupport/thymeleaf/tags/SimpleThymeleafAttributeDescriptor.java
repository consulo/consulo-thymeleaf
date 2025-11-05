package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.tags;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.xml.XmlAttributeDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

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
    public @NonNls String getName(PsiElement context) {
        return name;
    }

    @Override public String getName() { return name; }

    @Override
    public void init(PsiElement element) {

    }

    @Override public boolean isRequired() { return false; }
    @Override public boolean isFixed() { return false; }
    @Override public boolean hasIdType() { return false; }
    @Override public boolean hasIdRefType() { return false; }
    @Override public String getDefaultValue() { return null; }
    @Override public boolean isEnumerated() { return false; }
    @Override public String[] getEnumeratedValues() { return new String[0]; }

    @Override
    public @Nullable @NlsContexts.DetailedDescription String validateValue(XmlElement context, String value) {
        return null;
    }

}
