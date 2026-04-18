package com.mdrsolutions.thymeleaf.thymeleafsupport.springsecurity.attributes;

import com.intellij.xml.impl.BasicXmlAttributeDescriptor;
import com.mdrsolutions.thymeleaf.thymeleafsupport.Thymeleaf;
import consulo.language.psi.PsiElement;
import consulo.language.psi.meta.PsiPresentableMetaData;
import consulo.ui.image.Image;
import consulo.util.collection.ArrayUtil;
import consulo.xml.language.psi.XmlTag;
import jakarta.annotation.Nullable;

public class SpringSecurityAttributeDescriptor extends BasicXmlAttributeDescriptor implements PsiPresentableMetaData {
    public SpringSecurityAttributeDescriptor(String attributeName, XmlTag context) {
        info = new SpringSecurityAttributeInfo(attributeName);
        icon = Thymeleaf.ICON;
        name = attributeName;
        xmlTag = context;
    }

    private final String name;
    private final XmlTag xmlTag;
    private final SpringSecurityAttributeInfo info;
    private final Image icon;

    @Override
    @Nullable
    public String getTypeName() {
        return info.getTypeText();
    }

    @Override
    @Nullable
    public Image getIcon() {
        return icon;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public boolean hasIdType() {
        return name.equalsIgnoreCase("id");
    }

    @Override
    public boolean hasIdRefType() {
        return false;
    }

    @Override
    public boolean isEnumerated() {
        return !info.hasValue();
    }

    @Override
    public PsiElement getDeclaration() {
        return xmlTag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init(PsiElement element) {
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public String getDefaultValue() {
        return "";
    }

    @Override
    public String[] getEnumeratedValues() {
        return ArrayUtil.EMPTY_STRING_ARRAY;
    }

    @Override
    public Object[] getDependences() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }
}
