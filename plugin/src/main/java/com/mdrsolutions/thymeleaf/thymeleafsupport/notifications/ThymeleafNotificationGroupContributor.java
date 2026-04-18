package com.mdrsolutions.thymeleaf.thymeleafsupport.notifications;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationDisplayType;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;

import java.util.function.Consumer;

@ExtensionImpl
public class ThymeleafNotificationGroupContributor implements NotificationGroupContributor {

    public static final NotificationGroup THYMELEAF =
            new NotificationGroup("HtmlThymeleaf", LocalizeValue.localizeTODO("Thymeleaf"), NotificationDisplayType.BALLOON, true);

    public static final NotificationGroup LAYOUT =
            new NotificationGroup("HtmlThymeleafLayout", LocalizeValue.localizeTODO("Thymeleaf Layout"), NotificationDisplayType.BALLOON, true);

    public static final NotificationGroup SPRING_SECURITY =
            new NotificationGroup("HtmlThymeleafSpringSecurity", LocalizeValue.localizeTODO("Thymeleaf Spring Security"), NotificationDisplayType.BALLOON, true);

    public static final NotificationGroup URI_IGNORE_RESOURCE =
            new NotificationGroup("UriIgnoreResource", LocalizeValue.localizeTODO("URI Ignore Resource"), NotificationDisplayType.BALLOON, true);

    @Override
    public void contribute(Consumer<NotificationGroup> registrator) {
        registrator.accept(THYMELEAF);
        registrator.accept(LAYOUT);
        registrator.accept(SPRING_SECURITY);
        registrator.accept(URI_IGNORE_RESOURCE);
    }
}
