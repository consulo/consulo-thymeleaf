package com.mdrsolutions.thymeleaf.thymeleafsupport.actions;

import com.mdrsolutions.thymeleaf.thymeleafsupport.namespace.ThymeleafNamespaceRegistry;
import com.mdrsolutions.thymeleaf.thymeleafsupport.notifications.ThymeleafNotificationGroupContributor;
import consulo.language.editor.CommonDataKeys;
import consulo.language.file.FileTypeManager;
import consulo.language.file.LanguageFileType;
import consulo.language.psi.PsiFile;
import consulo.logging.Logger;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.ui.ex.action.ActionUpdateThread;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract class BaseThymeleafNamespaceAction extends AnAction {
    private static final Logger logger = Logger.getInstance(BaseThymeleafNamespaceAction.class);

    protected static final ThymeleafNamespaceRegistry.NamespaceAttribute THYMELEAF_SPRING_SECURITY_NAMESPACE = ThymeleafNamespaceRegistry.NamespaceAttribute.SPRING_SECURITY;
    protected static final String THYMELEAF_SPRING_SECURITY_NAMESPACE_URI = ThymeleafNamespaceRegistry.getNamespaces().get(THYMELEAF_SPRING_SECURITY_NAMESPACE);

    protected static final ThymeleafNamespaceRegistry.NamespaceAttribute THYMELEAF_LAYOUT_NAMESPACE = ThymeleafNamespaceRegistry.NamespaceAttribute.LAYOUT;
    protected static final String THYMELEAF_LAYOUT_NAMESPACE_URI = ThymeleafNamespaceRegistry.getNamespaces().get(THYMELEAF_LAYOUT_NAMESPACE);

    protected static final ThymeleafNamespaceRegistry.NamespaceAttribute THYMELEAF_NAMESPACE = ThymeleafNamespaceRegistry.NamespaceAttribute.THYMELEAF;
    protected static final String THYMELEAF_NAMESPACE_URI = ThymeleafNamespaceRegistry.getNamespaces().get(THYMELEAF_NAMESPACE);

    private final NotificationGroup myGroup;

    public BaseThymeleafNamespaceAction(NotificationGroup group) {
        this.myGroup = group;
    }

    protected NotificationGroup getNotificationGroup() {
        return this.myGroup;
    }

    @Override
    public void update(@Nonnull AnActionEvent anActionEvent) {
        PsiFile selectedFile = getSelectedPropertiesFile(anActionEvent, false, getNotificationGroup());
        anActionEvent.getPresentation().setEnabledAndVisible(selectedFile != null);
    }

    @Nullable
    protected PsiFile getSelectedPropertiesFile(@Nonnull AnActionEvent anActionEvent, boolean showNotifications, NotificationGroup group) {
        PsiFile selectedFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (selectedFile == null) {
            if (showNotifications) {
                Notifications.Bus.notify(new Notification(group, "No file selected", "Please select an HTML file first", NotificationType.ERROR));
            }
            return null;
        }
        LanguageFileType html = (LanguageFileType) FileTypeManager.getInstance().getStdFileType("HTML");
        if (!html.equals(selectedFile.getFileType())) {
            if (showNotifications) {
                Notifications.Bus.notify(new Notification(group, "Incorrect file selected", "Please select an HTML file", NotificationType.ERROR));
            }
            return null;
        }
        return selectedFile;
    }

    @Override
    @Nonnull
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
