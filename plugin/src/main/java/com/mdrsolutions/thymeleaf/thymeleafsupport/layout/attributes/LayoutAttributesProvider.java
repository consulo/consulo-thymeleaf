package com.mdrsolutions.thymeleaf.thymeleafsupport.layout.attributes;

import com.intellij.xml.XmlAttributeDescriptorsProvider;
import consulo.annotation.component.ExtensionImpl;
import consulo.logging.Logger;
import consulo.xml.descriptor.XmlAttributeDescriptor;
import consulo.xml.psi.impl.source.html.dtd.HtmlElementDescriptorImpl;
import consulo.xml.language.psi.XmlTag;
import jakarta.annotation.Nullable;

@ExtensionImpl
public class LayoutAttributesProvider implements XmlAttributeDescriptorsProvider {
    private static final Logger logger = Logger.getInstance(LayoutAttributesProvider.class);

    @Override
    public XmlAttributeDescriptor[] getAttributeDescriptors(XmlTag context) {
        return new XmlAttributeDescriptor[0];
    }

    @Override
    @Nullable
    public XmlAttributeDescriptor getAttributeDescriptor(String attributeName, XmlTag context) {
        logger.debug("LayoutAttributesProvider.getAttributeDescriptors(...) - attributeName ={" + attributeName + "}, xmlTag={" + context + "}");

        if (!(context.getDescriptor() instanceof HtmlElementDescriptorImpl)) {
            return null;
        }
        LayoutAttributeInfo layoutAttributeInfo = new LayoutAttributeInfo(attributeName);

        if (layoutAttributeInfo.isThymeleaf()) {
            logger.debug("this is a thymeleaf attribute");
            return new LayoutAttributeDescriptor(attributeName, context);
        }
        return null;
    }
}
