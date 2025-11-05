package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.tags;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

public class ThymeleafBlockInspectionSuppressor implements InspectionSuppressor {

    private static final Logger logger = Logger.getInstance(ThymeleafBlockInspectionSuppressor.class);

    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String inspectionId) {
        // Suppress for our specific tag and inspection
        logger.info("ThymeleafBlockInspectionSuppressor inspectionId: " + inspectionId + " element: " + element.getText());
        if (element instanceof XmlTag tag) {
            logger.info("isSuppressedFor --> " + tag.getName());
            if ("th:block".equalsIgnoreCase(tag.getName())) {
                // The inspection for "cannot resolve symbol" is called "UnresolvedXmlReference"
                logger.info("attempting to return UnresolvedXmlReference");
                return "UnresolvedXmlReference".equals(inspectionId);
            }
        }
        return false;
    }

    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@NotNull PsiElement element, @NotNull String inspectionId) {
        // No quick fixes offered for suppression in this case
        logger.info("getSuppressActions called");
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}
