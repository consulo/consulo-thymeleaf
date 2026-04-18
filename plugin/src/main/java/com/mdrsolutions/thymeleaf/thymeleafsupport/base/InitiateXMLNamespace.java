package com.mdrsolutions.thymeleaf.thymeleafsupport.base;

import com.mdrsolutions.thymeleaf.thymeleafsupport.layout.attributes.LayoutAttributeCompletionProvider;
import com.mdrsolutions.thymeleaf.thymeleafsupport.springsecurity.attributes.SpringSecurityAttributeCompletionProvider;
import com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.attributes.ThymeleafAttributeCompletionProvider;
import consulo.application.ApplicationManager;
import consulo.language.editor.WriteCommandAction;
import consulo.language.editor.completion.lookup.InsertionContext;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.logging.Logger;
import consulo.xml.language.psi.XmlTag;

public class InitiateXMLNamespace {

    private static final Logger logger = Logger.getInstance(InitiateXMLNamespace.class);
    private static final String HTML = "html";

    public static void writeNamespace(String text, InsertionContext insertionContext) {

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(insertionContext.getProject());
        psiDocumentManager.commitAllDocuments();

        final BaseAttributeCompletionProvider thymeProvider = new ThymeleafAttributeCompletionProvider();
        final BaseAttributeCompletionProvider layoutProvider = new LayoutAttributeCompletionProvider();
        final BaseAttributeCompletionProvider secProvider = new SpringSecurityAttributeCompletionProvider();

        if (text.startsWith(thymeProvider.getAttributeStartingChars())) {
            ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(insertionContext.getProject(), () -> {
                PsiElement element = insertionContext.getFile().findElementAt(insertionContext.getStartOffset());
                if (element != null) {
                    XmlTag rootTag = findRootTag(element);
                    if (rootTag != null) {
                        checkAndInsertNamespace(
                                rootTag,
                                insertionContext.getFile(),
                                thymeProvider.getNamespaceAttr(),
                                thymeProvider.getNamespaceValue());
                    }
                }
            }));
        } else if (text.startsWith(layoutProvider.getAttributeStartingChars())) {
            ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(insertionContext.getProject(), () -> {
                PsiElement element = insertionContext.getFile().findElementAt(insertionContext.getStartOffset());
                if (element != null) {
                    XmlTag rootTag = findRootTag(element);
                    if (rootTag != null) {
                        checkAndInsertNamespace(
                                rootTag,
                                insertionContext.getFile(),
                                layoutProvider.getNamespaceAttr(),
                                layoutProvider.getNamespaceValue());
                    }
                }
            }));
        } else if (text.startsWith(secProvider.getAttributeStartingChars())) {
            ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(insertionContext.getProject(), () -> {
                PsiElement element = insertionContext.getFile().findElementAt(insertionContext.getStartOffset());
                if (element != null) {
                    XmlTag rootTag = findRootTag(element);
                    if (rootTag != null) {
                        checkAndInsertNamespace(
                                rootTag,
                                insertionContext.getFile(),
                                secProvider.getNamespaceAttr(),
                                secProvider.getNamespaceValue());
                    }
                }
            }));
        }
    }

    private static void checkAndInsertNamespace(XmlTag rootTag, PsiFile file, String namespaceAttr, String namespaceValue) {
        if (rootTag != null && rootTag.getAttribute(namespaceAttr) == null) {
            WriteCommandAction.runWriteCommandAction(file.getProject(), () -> {
                rootTag.setAttribute(namespaceAttr, namespaceValue);
            });
        }
    }

    protected static XmlTag findRootTag(PsiElement currentElement) {
        PsiElement parentElement = currentElement;
        while (parentElement != null) {
            if (parentElement instanceof XmlTag xmlTag) {
                if (HTML.equalsIgnoreCase(xmlTag.getName())) {
                    return xmlTag;
                }
            }
            parentElement = parentElement.getParent();
        }
        return null;
    }
}
