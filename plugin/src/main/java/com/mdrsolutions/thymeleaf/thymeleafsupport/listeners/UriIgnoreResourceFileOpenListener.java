package com.mdrsolutions.thymeleaf.thymeleafsupport.listeners;

import com.mdrsolutions.thymeleaf.thymeleafsupport.namespace.ThymeleafNamespaceRegistry;
import com.mdrsolutions.thymeleaf.thymeleafsupport.notifications.ThymeleafNotificationGroupContributor;
import com.intellij.xml.util.HtmlUtil;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicImpl;
import consulo.application.ApplicationManager;
import consulo.fileEditor.FileEditor;
import consulo.fileEditor.FileEditorManager;
import consulo.fileEditor.TextEditor;
import consulo.fileEditor.event.FileEditorManagerListener;
import consulo.logging.Logger;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.virtualFileSystem.VirtualFile;
import consulo.xml.javaee.ExternalResourceManagerEx;
import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.List;

@TopicImpl(ComponentScope.PROJECT)
public final class UriIgnoreResourceFileOpenListener implements FileEditorManagerListener {
    private static final Logger logger = Logger.getInstance(UriIgnoreResourceFileOpenListener.class);
    private static final String[] IGNORED_RESOURCES = ThymeleafNamespaceRegistry.getNamespaces().values().toArray(new String[0]);

    @Override
    public void fileOpened(@Nonnull FileEditorManager source, @Nonnull VirtualFile file) {
        if (!HtmlUtil.isHtmlFile(file)) {
            return;
        }
        for (FileEditor editor : source.getEditors(file)) {
            if (editor instanceof TextEditor) {
                initializeIgnoredResources();
                return;
            }
        }
    }

    private void initializeIgnoredResources() {
        logger.info("UriIgnoreResourceFileOpenListener.initializeIgnoredResources()");
        ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
            ExternalResourceManagerEx externalResourceManager = ExternalResourceManagerEx.getInstanceEx();
            if (externalResourceManager == null) {
                logger.info("externalResourceManager is null - operation skipped");
                return;
            }

            List<String> currentIgnoredResources = Arrays.asList(externalResourceManager.getIgnoredResources());
            boolean added = false;
            for (String resource : IGNORED_RESOURCES) {
                if (!currentIgnoredResources.contains(resource)) {
                    externalResourceManager.addIgnoredResource(resource);
                    added = true;
                }
            }

            if (added) {
                Notifications.Bus.notify(new Notification(
                        ThymeleafNotificationGroupContributor.URI_IGNORE_RESOURCE,
                        "DTD and URI Resources",
                        "Related Thymeleaf Layout Namespaces have been safely ignored.",
                        NotificationType.INFORMATION));
            }
        }));
    }
}
