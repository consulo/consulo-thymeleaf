package com.mdrsolutions.thymeleaf.thymeleafsupport.spring;

import com.intellij.java.indexing.search.searches.AnnotatedElementsSearch;
import com.intellij.java.language.psi.JavaPsiFacade;
import com.intellij.java.language.psi.PsiAnnotation;
import com.intellij.java.language.psi.PsiAnnotationMemberValue;
import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiMethod;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ControllerUrlUtil {
    private static final Set<String> MAPPING_ANNOTATIONS = Set.of(
            "org.springframework.web.bind.annotation.RequestMapping",
            "org.springframework.web.bind.annotation.GetMapping",
            "org.springframework.web.bind.annotation.PostMapping",
            "org.springframework.web.bind.annotation.PutMapping",
            "org.springframework.web.bind.annotation.DeleteMapping",
            "org.springframework.web.bind.annotation.PatchMapping"
    );

    private static final Set<String> CONTROLLER_ANNOTATIONS = Set.of(
            "org.springframework.stereotype.Controller",
            "org.springframework.web.bind.annotation.RestController"
    );

    public static List<ControllerMappingInfo> getControllerUrls(@Nonnull Project project) {
        List<ControllerMappingInfo> result = new ArrayList<>();

        for (String controllerAnnotation : CONTROLLER_ANNOTATIONS) {
            PsiClass annoClass = JavaPsiFacade.getInstance(project)
                    .findClass(controllerAnnotation, GlobalSearchScope.allScope(project));
            if (annoClass == null) continue;

            Collection<PsiClass> controllerClasses = AnnotatedElementsSearch
                    .searchPsiClasses(annoClass, GlobalSearchScope.projectScope(project)).findAll();
            for (PsiClass controllerClass : controllerClasses) {
                String classPrefix = extractMappingValue(controllerClass.getAnnotations());

                for (PsiMethod method : controllerClass.getMethods()) {
                    String methodMapping = extractMappingValue(method.getAnnotations());
                    String httpMethod = extractHttpMethod(method.getAnnotations());
                    if (methodMapping != null && !methodMapping.isEmpty()) {
                        StringBuilder fullPath = new StringBuilder();
                        if (classPrefix != null && !classPrefix.isEmpty()) {
                            String prefix = classPrefix.endsWith("/") ?
                                    classPrefix.substring(0, classPrefix.length() - 1) : classPrefix;
                            fullPath.append(prefix);
                        }
                        if (!methodMapping.startsWith("/")) {
                            fullPath.append("/");
                        }
                        fullPath.append(methodMapping);
                        ControllerMappingInfo info = new ControllerMappingInfo(
                                fullPath.toString(),
                                httpMethod,
                                controllerClass.getName(),
                                method.getName()
                        );
                        result.add(info);
                    }
                }
            }
        }
        return result;
    }

    private static String extractHttpMethod(PsiAnnotation[] annotations) {
        for (PsiAnnotation annotation : annotations) {
            String qname = annotation.getQualifiedName();
            if (qname == null) continue;
            if (qname.endsWith("GetMapping")) return "GET";
            if (qname.endsWith("PostMapping")) return "POST";
            if (qname.endsWith("PutMapping")) return "PUT";
            if (qname.endsWith("DeleteMapping")) return "DELETE";
            if (qname.endsWith("PatchMapping")) return "PATCH";
            if (qname.endsWith("RequestMapping")) {
                PsiAnnotationMemberValue methodValue = annotation.findDeclaredAttributeValue("method");
                if (methodValue != null) {
                    String text = methodValue.getText();
                    if (text.contains("GET")) return "GET";
                    if (text.contains("POST")) return "POST";
                    if (text.contains("PUT")) return "PUT";
                    if (text.contains("DELETE")) return "DELETE";
                    if (text.contains("PATCH")) return "PATCH";
                }
                return "ANY";
            }
        }
        return "ANY";
    }

    private static String extractMappingValue(PsiAnnotation[] annotations) {
        for (PsiAnnotation annotation : annotations) {
            String qname = annotation.getQualifiedName();
            if (qname != null && MAPPING_ANNOTATIONS.contains(qname)) {
                PsiAnnotationMemberValue value = annotation.findDeclaredAttributeValue("value");
                if (value == null) value = annotation.findDeclaredAttributeValue("path");
                if (value != null) {
                    String text = value.getText();
                    if (text.startsWith("{")) text = text.substring(1, text.length() - 1);
                    text = text.replaceAll("\"", "").trim();
                    return text;
                }
            }
        }
        return null;
    }
}
