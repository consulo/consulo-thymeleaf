package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.attributes;

import com.intellij.xml.XmlAttributeDescriptorsProvider;
import consulo.annotation.component.ExtensionImpl;
import consulo.logging.Logger;
import consulo.xml.descriptor.XmlAttributeDescriptor;
import consulo.xml.psi.impl.source.html.dtd.HtmlElementDescriptorImpl;
import consulo.xml.language.psi.XmlTag;
import jakarta.annotation.Nullable;

@ExtensionImpl
public class ThymeleafAttributesProvider implements XmlAttributeDescriptorsProvider {
    private static final Logger logger = Logger.getInstance(ThymeleafAttributesProvider.class);

    @Override
    public XmlAttributeDescriptor[] getAttributeDescriptors(XmlTag context) {
        return new XmlAttributeDescriptor[0];
    }

    @Override
    @Nullable
    public XmlAttributeDescriptor getAttributeDescriptor(String attributeName, XmlTag context) {
        logger.debug("AttributesProvider.getAttributeDescriptor(...) - attributeName ={" + attributeName + "}, xmlTag={" + context + "}");

        if (!(context.getDescriptor() instanceof HtmlElementDescriptorImpl)) {
            return null;
        }
        ThymeleafAttributeInfo thymeleafAttributeInfo = new ThymeleafAttributeInfo(attributeName);

        if (thymeleafAttributeInfo.isThymeleaf()) {
            logger.debug("this is a thymeleaf attribute");
            return new ThymeleafAttributeDescriptor(attributeName, context);
        }
        return null;
    }
}
