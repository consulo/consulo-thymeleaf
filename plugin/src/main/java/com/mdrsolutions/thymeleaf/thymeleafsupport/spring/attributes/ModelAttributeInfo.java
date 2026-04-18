package com.mdrsolutions.thymeleaf.thymeleafsupport.spring.attributes;

import com.intellij.psi.PsiClass;

import java.util.Set;

public class ModelAttributeInfo {
    public final String name;
    public final PsiClass psiClass;
    public final Set<String> viewNames; // e.g. ["user-info", "dashboard"]

    public ModelAttributeInfo(String name, PsiClass psiClass, Set<String> viewNames) {
        this.name = name;
        this.psiClass = psiClass;
        this.viewNames = viewNames;
    }
}

