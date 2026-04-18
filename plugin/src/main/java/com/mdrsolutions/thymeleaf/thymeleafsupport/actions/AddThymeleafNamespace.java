package com.mdrsolutions.thymeleaf.thymeleafsupport.actions;

import com.mdrsolutions.thymeleaf.thymeleafsupport.notifications.ThymeleafNotificationGroupContributor;
import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.application.ApplicationManager;
import consulo.logging.Logger;
import consulo.language.psi.PsiFile;
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

@ActionImpl(id = "HtmlThymeleaf", parents = @ActionParentRef(@ActionRef(id = "ProjectViewPopupMenu")))
public class AddThymeleafNamespace extends BaseThymeleafNamespaceAction {
    private static final Logger logger = Logger.getInstance(AddThymeleafNamespace.class);

    public AddThymeleafNamespace() {
        super(ThymeleafNotificationGroupContributor.THYMELEAF);
        getTemplatePresentation().setText("Add Thymeleaf Namespace");
        getTemplatePresentation().setDescription("Add Thymeleaf Namespace to html");
    }

    @Override
    public void actionPerformed(@Nonnull AnActionEvent anActionEvent) {
        logger.info("HtmlThymeleaf.actionPerformed(...)");
        PsiFile selectedFile = this.getSelectedPropertiesFile(anActionEvent, true, getNotificationGroup());
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
                if (htmlElement != null && !htmlElement.hasAttr(THYMELEAF_NAMESPACE.getAttribute())) {
                    htmlElement.attr(THYMELEAF_NAMESPACE.getAttribute(), THYMELEAF_NAMESPACE_URI);
                }

                String newModifiedHtml = document.outerHtml();
                htmlFile.setBinaryContent(newModifiedHtml.getBytes(StandardCharsets.UTF_8));

                Notifications.Bus.notify(new Notification(getNotificationGroup(), "File transformed",
                        "Thymeleaf Namespace '" + THYMELEAF_NAMESPACE.getAttribute() + "=\"" + THYMELEAF_NAMESPACE_URI + "\"' added to html tag successfully.",
                        NotificationType.INFORMATION));
            } catch (Exception e) {
                Notifications.Bus.notify(new Notification(getNotificationGroup(), "Cannot write file", e.getMessage(), NotificationType.ERROR));
            }
        });
    }
}
