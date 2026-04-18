package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.tags;

import consulo.annotation.component.ExtensionImpl;
import consulo.logging.Logger;
import consulo.xml.descriptor.XmlElementDescriptor;
import consulo.xml.psi.impl.source.xml.XmlElementDescriptorProvider;
import consulo.xml.language.psi.XmlTag;

@ExtensionImpl
public class ThymeleafElementDescriptorProvider implements XmlElementDescriptorProvider {

    private static final Logger logger = Logger.getInstance(ThymeleafElementDescriptorProvider.class);

    @Override
    public XmlElementDescriptor getDescriptor(XmlTag tag) {
        if ("th:block".equalsIgnoreCase(tag.getName())) {
            logger.info("return new ThymeleafBlockTagDescriptor");
            return new ThymeleafBlockTagDescriptor();
        }
        return null;
    }
}
