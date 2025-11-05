package com.mdrsolutions.thymeleaf.thymeleafsupport.spring.attributes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SpringModelAttributeUtil {

    // Add relevant Spring controller annotations
    private static final Set<String> CONTROLLER_ANNOTATIONS = Set.of(
            "org.springframework.stereotype.Controller",
            "org.springframework.web.bind.annotation.RestController"
    );

    /**
     * Returns all model attributes as a list of ModelAttributeInfo (name, type)
     */
    public static List<ModelAttributeInfo> getModelAttributes(Project project) {
        List<ModelAttributeInfo> results = new ArrayList<>();

        for (String controllerAnnotation : CONTROLLER_ANNOTATIONS) {
            PsiClass annoClass = JavaPsiFacade.getInstance(project)
                    .findClass(controllerAnnotation, GlobalSearchScope.allScope(project));
            if (annoClass == null) continue;

            Collection<PsiClass> controllers = AnnotatedElementsSearch.searchPsiClasses(
                    annoClass, GlobalSearchScope.projectScope(project)).findAll();

            for (PsiClass controller : controllers) {
                for (PsiMethod method : controller.getMethods()) {
                    Set<String> viewNames = getViewNamesFromMethod(method);

                    // 1. Handle @ModelAttribute methods
                    processModelAttributeMethods(method, results, viewNames);

                    // 2. Handle @ModelAttribute parameters
                    processModelAttributeParameters(method, results, viewNames);

                    // 3. Handle model.addAttribute() calls
                    processModelAddAttributeCalls(method, results, viewNames);
                }
            }
        }
        return results;
    }

    private static Set<String> getViewNamesFromMethod(PsiMethod method) {
        Set<String> viewNames = new HashSet<>();
        method.accept(new JavaRecursiveElementWalkingVisitor() {
            @Override
            public void visitReturnStatement(@NotNull PsiReturnStatement statement) {
                PsiExpression expr = statement.getReturnValue();
                if (expr instanceof PsiLiteralExpression) {
                    Object value = ((PsiLiteralExpression) expr).getValue();
                    if (value instanceof String) {
                        String viewName = ((String) value).replaceAll("\"", "");
                        if (viewName.endsWith(".html")) {
                            viewNames.add(viewName.substring(0, viewName.length() - 5));
                        } else {
                            viewNames.add(viewName);
                        }
                    }
                }
                super.visitReturnStatement(statement);
            }
        });
        return viewNames;
    }

    private static void processModelAttributeMethods(PsiMethod method,
                                                     List<ModelAttributeInfo> results,
                                                     Set<String> viewNames) {
        PsiAnnotation modelAttributeAnno = method.getAnnotation(
                "org.springframework.web.bind.annotation.ModelAttribute");
        if (modelAttributeAnno != null) {
            String name = method.getName();
            PsiAnnotationMemberValue valueAttr = modelAttributeAnno.findAttributeValue("value");
            if (valueAttr != null) {
                name = valueAttr.getText().replaceAll("\"", "");
            }
            PsiType returnType = method.getReturnType();
            if (returnType != null) {
                PsiClass typeClass = PsiUtil.resolveClassInClassTypeOnly(returnType);
                if (typeClass != null) {
                    results.add(new ModelAttributeInfo(name, typeClass, new HashSet<>(viewNames)));
                }
            }
        }
    }

    private static void processModelAttributeParameters(PsiMethod method,
                                                        List<ModelAttributeInfo> results,
                                                        Set<String> viewNames) {
        for (PsiParameter param : method.getParameterList().getParameters()) {
            PsiAnnotation modelAttributeAnno = param.getAnnotation(
                    "org.springframework.web.bind.annotation.ModelAttribute");
            if (modelAttributeAnno != null) {
                String name = param.getName();
                PsiAnnotationMemberValue val = modelAttributeAnno.findAttributeValue("value");
                if (val != null) name = val.getText().replaceAll("\"", "");
                PsiType type = param.getType();
                PsiClass typeClass = PsiUtil.resolveClassInClassTypeOnly(type);
                if (typeClass != null) {
                    results.add(new ModelAttributeInfo(name, typeClass, new HashSet<>(viewNames)));
                }
            }

            // Also handle Model/ModelMap parameters
            if (isModelType(param.getType())) {
                method.accept(new JavaRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expr) {
                        checkForAddAttributeCall(expr, param.getName(), results, viewNames);
                        super.visitMethodCallExpression(expr);
                    }
                });
            }
        }
    }

    private static void processModelAddAttributeCalls(PsiMethod method,
                                                      List<ModelAttributeInfo> results,
                                                      Set<String> viewNames) {
        method.accept(new JavaRecursiveElementWalkingVisitor() {
            @Override
            public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expr) {
                checkForAddAttributeCall(expr, "model", results, viewNames);
                super.visitMethodCallExpression(expr);
            }
        });
    }

    private static void checkForAddAttributeCall(PsiMethodCallExpression expr,
                                                 String expectedQualifierName,
                                                 List<ModelAttributeInfo> results,
                                                 Set<String> viewNames) {
        PsiReferenceExpression ref = expr.getMethodExpression();
        if (ref.getReferenceName() != null && ref.getReferenceName().equals("addAttribute")) {
            PsiExpression qualifier = ref.getQualifierExpression();
            if (qualifier != null && qualifier.getText().equals(expectedQualifierName)) {
                PsiExpression[] args = expr.getArgumentList().getExpressions();
                if (args.length >= 2 && args[0] instanceof PsiLiteralExpression) {
                    String name = ((PsiLiteralExpression) args[0]).getValue() + "";
                    PsiType type = args[1].getType();
                    PsiClass typeClass = PsiUtil.resolveClassInClassTypeOnly(type);
                    if (typeClass != null && !"null".equals(name)) {
                        results.add(new ModelAttributeInfo(name, typeClass, new HashSet<>(viewNames)));
                    }
                }
            }
        }
    }

    private static boolean isModelType(PsiType type) {
        if (type == null) return false;
        String canonicalText = type.getCanonicalText();
        return canonicalText.equals("org.springframework.ui.Model") ||
                canonicalText.equals("org.springframework.ui.ModelMap") ||
                canonicalText.equals("java.util.Map") ||
                canonicalText.equals("org.springframework.web.servlet.ModelAndView");
    }
}