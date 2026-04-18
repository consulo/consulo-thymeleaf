package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.tags;

import consulo.annotation.component.ExtensionImpl;
import consulo.html.language.HTMLLanguage;
import consulo.language.Language;
import consulo.language.editor.inspection.InspectionSuppressor;
import consulo.language.editor.inspection.SuppressQuickFix;
import consulo.language.psi.PsiElement;
import consulo.logging.Logger;
import consulo.xml.language.psi.XmlTag;
import jakarta.annotation.Nonnull;

@ExtensionImpl
public class ThymeleafBlockInspectionSuppressor implements InspectionSuppressor {

    private static final Logger logger = Logger.getInstance(ThymeleafBlockInspectionSuppressor.class);

    @Override
    public boolean isSuppressedFor(@Nonnull PsiElement element, @Nonnull String inspectionId) {
        logger.info("ThymeleafBlockInspectionSuppressor inspectionId: " + inspectionId + " element: " + element.getText());
        if (element instanceof XmlTag tag) {
            logger.info("isSuppressedFor --> " + tag.getName());
            if ("th:block".equalsIgnoreCase(tag.getName())) {
                logger.info("attempting to return UnresolvedXmlReference");
                return "UnresolvedXmlReference".equals(inspectionId);
            }
        }
        return false;
    }

    @Override
    @Nonnull
    public SuppressQuickFix[] getSuppressActions(@Nonnull PsiElement element, @Nonnull String inspectionId) {
        logger.info("getSuppressActions called");
        return SuppressQuickFix.EMPTY_ARRAY;
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return HTMLLanguage.INSTANCE;
    }
}
