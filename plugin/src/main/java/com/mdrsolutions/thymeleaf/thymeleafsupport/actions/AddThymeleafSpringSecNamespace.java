package com.mdrsolutions.thymeleaf.thymeleafsupport.actions;

import com.mdrsolutions.thymeleaf.thymeleafsupport.notifications.ThymeleafNotificationGroupContributor;
import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.application.ApplicationManager;
import consulo.language.psi.PsiFile;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.ui.ex.action.AnActionEvent;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.nio.charset.StandardCharsets;

@ActionImpl(id = "HtmlThymeleafSpringSecurity", parents = @ActionParentRef(@ActionRef(id = "ProjectViewPopupMenu")))
public class AddThymeleafSpringSecNamespace extends BaseThymeleafNamespaceAction {
    private static final Logger logger = Logger.getInstance(AddThymeleafSpringSecNamespace.class);

    public AddThymeleafSpringSecNamespace() {
        super(ThymeleafNotificationGroupContributor.SPRING_SECURITY);
        getTemplatePresentation().setText("Add Thymeleaf Spring Security Namespace");
        getTemplatePresentation().setDescription("Add Thymeleaf Spring Security namespace to html");
    }

    @Override
    public void actionPerformed(@Nonnull AnActionEvent anActionEvent) {
        logger.info("AddThymeleafSpringSecNamespace.actionPerformed(...)");
        PsiFile selectedFile = getSelectedPropertiesFile(anActionEvent, true, getNotificationGroup());
        if (selectedFile == null) {
            return;
        }
        Project project = anActionEvent.getData(consulo.project.Project.KEY);
        if (project == null) {
            return;
        }
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                VirtualFile htmlFile = selectedFile.getVirtualFile();
                String content = new String(htmlFile.contentsToByteArray(), StandardCharsets.UTF_8);

                Document document = Jsoup.parse(content);

                Element htmlElement = document.selectFirst("html");
                if (htmlElement != null && !htmlElement.hasAttr(THYMELEAF_SPRING_SECURITY_NAMESPACE.getAttribute())) {
                    htmlElement.attr(THYMELEAF_SPRING_SECURITY_NAMESPACE.getAttribute(), THYMELEAF_SPRING_SECURITY_NAMESPACE_URI);
                }

                String newModifiedHtml = document.outerHtml();
                htmlFile.setBinaryContent(newModifiedHtml.getBytes(StandardCharsets.UTF_8));

                Notifications.Bus.notify(new Notification(getNotificationGroup(), "File transformed",
                        "Thymeleaf Namespace '" + THYMELEAF_SPRING_SECURITY_NAMESPACE.getAttribute() + "=\"" + THYMELEAF_SPRING_SECURITY_NAMESPACE_URI + "\"' added to html tag successfully.",
                        NotificationType.INFORMATION));
            } catch (Exception e) {
                Notifications.Bus.notify(new Notification(getNotificationGroup(), "Cannot write file", e.getMessage(), NotificationType.ERROR));
            }
        });
    }
}
