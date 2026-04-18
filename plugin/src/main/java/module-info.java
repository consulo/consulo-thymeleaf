/**
 * @author Michael D. Rodgers, Jr
 */
open module com.mdrsolutions.thymeleaf.thymeleafsupport {
    requires consulo.application.api;
    requires consulo.code.editor.api;
    requires consulo.component.api;
    requires consulo.disposer.api;
    requires consulo.document.api;
    requires consulo.file.editor.api;
    requires consulo.ide.api;
    requires consulo.language.api;
    requires consulo.language.impl;
    requires consulo.language.editor.api;
    requires consulo.localize.api;
    requires consulo.logging.api;
    requires consulo.project.api;
    requires consulo.project.ui.api;
    requires consulo.ui.api;
    requires consulo.ui.ex.api;
    requires consulo.util.collection;
    requires consulo.util.dataholder;
    requires consulo.util.lang;
    requires consulo.virtual.file.system.api;

    requires com.intellij.xml;
    requires com.intellij.xml.api;
    requires com.intellij.xml.html.api;
    requires consulo.java;
    requires consulo.java.language.api;
    requires consulo.java.indexing.api;

    requires org.jsoup;
}
