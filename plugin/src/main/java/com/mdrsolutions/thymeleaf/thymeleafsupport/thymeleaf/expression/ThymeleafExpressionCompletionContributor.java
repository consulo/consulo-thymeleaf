package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression;

import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiField;
import com.intellij.java.language.psi.PsiModifier;
import com.intellij.java.language.psi.PsiType;
import com.intellij.java.language.psi.util.PsiUtil;
import com.mdrsolutions.thymeleaf.thymeleafsupport.spring.attributes.ModelAttributeInfo;
import com.mdrsolutions.thymeleaf.thymeleafsupport.spring.attributes.SpringModelAttributeUtil;
import consulo.annotation.component.ExtensionImpl;
import consulo.html.language.HTMLLanguage;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.CompletionType;
import consulo.language.editor.completion.CompletionUtilCore;
import consulo.language.editor.completion.lookup.InsertHandler;
import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.pattern.PlatformPatterns;
import consulo.language.pattern.StandardPatterns;
import consulo.language.psi.PsiElement;
import consulo.language.util.ProcessingContext;
import consulo.project.Project;
import consulo.util.dataholder.Key;
import consulo.util.lang.StringUtil;
import consulo.xml.language.psi.pattern.XmlPatterns;
import consulo.xml.language.psi.XmlAttribute;
import consulo.xml.language.psi.XmlAttributeValue;
import consulo.xml.language.psi.XmlTag;
import jakarta.annotation.Nonnull;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;

@ExtensionImpl
public class ThymeleafExpressionCompletionContributor extends CompletionContributor {

    public static final Key<String> FULL_PROP_CHAIN_KEY = Key.create("FULL_PROP_CHAIN");
    private static final Map<PsiClass, Set<String>> PROPERTY_CHAIN_CACHE = new WeakHashMap<>();

    public ThymeleafExpressionCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .withParent(XmlPatterns.xmlAttributeValue()
                                .withParent(
                                        XmlPatterns.xmlAttribute().withName(
                                                StandardPatterns.string().startsWith("th:")
                                        )
                                )
                        ),
                new CompletionProvider() {
                    @Override
                    public void addCompletions(@Nonnull CompletionParameters parameters,
                                               @Nonnull ProcessingContext context,
                                               @Nonnull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        PsiElement parent = position.getParent();

                        if (!(parent instanceof XmlAttributeValue)) return;

                        PsiElement grandparent = parent.getParent();
                        if (!(grandparent instanceof XmlAttribute xmlAttribute)) return;

                        String attributeName = xmlAttribute.getName();
                        Project project = parameters.getEditor().getProject();

                        if (project == null) return;

                        for (ThymeleafExpressionSuggester.ExpressionSuggestion suggestion :
                                ThymeleafExpressionSuggester.getSuggestionsForAttribute(attributeName)) {
                            result.addElement(
                                    LookupElementBuilder.create(suggestion.template)
                                            .withPresentableText(suggestion.template)
                                            .withTypeText(suggestion.type, true)
                            );
                        }

                        String valueText = ((XmlAttributeValue) parent).getValue();

                        if (valueText.contains("${")) {
                            handleDollarSignExpression(parameters, parent, result, project, valueText);
                        } else if (valueText.contains("*{")) {
                            handleAsteriskExpression(parameters, parent, result, project, valueText);
                        }
                    }
                }
        );
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return HTMLLanguage.INSTANCE;
    }

    private void handleAsteriskExpression(@Nonnull CompletionParameters parameters,
                                          @Nonnull PsiElement parent,
                                          @Nonnull CompletionResultSet result,
                                          @Nonnull Project project,
                                          @Nonnull String valueText) {
        int attributeValueStartOffsetInFile = ((XmlAttributeValue) parent).getValueTextRange().getStartOffset();
        int caretOffsetInValueText = parameters.getOffset() - attributeValueStartOffsetInFile;
        int exprStartInValueText = valueText.lastIndexOf("*{", caretOffsetInValueText);
        if (exprStartInValueText == -1) {
            return;
        }

        int exprBodyStartInValueText = exprStartInValueText + 2;

        String typedPrefix;
        int dummyIdentifierPosInValueText = valueText.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER, exprBodyStartInValueText);

        if (dummyIdentifierPosInValueText != -1) {
            typedPrefix = valueText.substring(exprBodyStartInValueText, dummyIdentifierPosInValueText);
        } else {
            typedPrefix = valueText.substring(exprBodyStartInValueText, caretOffsetInValueText);
        }
        typedPrefix = typedPrefix.trim();

        String currentCompletionSegment;
        int lastDotIndexInTypedPrefix = typedPrefix.lastIndexOf('.');
        if (lastDotIndexInTypedPrefix != -1) {
            currentCompletionSegment = typedPrefix.substring(lastDotIndexInTypedPrefix + 1);
        } else {
            currentCompletionSegment = typedPrefix;
        }

        CompletionResultSet exprResult = result.withPrefixMatcher(currentCompletionSegment);

        PsiElement xmlTag = parent.getParent().getParent();
        String selectionExpression = getThObjectExpression(xmlTag);

        if (selectionExpression == null) {
            selectionExpression = getThEachExpression(xmlTag);
        }

        if (selectionExpression != null) {
            PsiClass selectionClass = findClassForExpression(selectionExpression, project);

            if (selectionClass != null) {
                Set<String> propertyChains = new TreeSet<>();
                collectPropertyChains(selectionClass, "", propertyChains, 4);

                addFilteredCompletions(exprResult, propertyChains, typedPrefix, new AsteriskInsertHandler());
            }
        }
    }

    private void handleDollarSignExpression(@Nonnull CompletionParameters parameters,
                                            @Nonnull PsiElement parent,
                                            @Nonnull CompletionResultSet result,
                                            @Nonnull Project project,
                                            @Nonnull String valueText) {
        int attributeValueStartOffsetInFile = ((XmlAttributeValue) parent).getValueTextRange().getStartOffset();
        int caretOffsetInValueText = parameters.getOffset() - attributeValueStartOffsetInFile;
        int exprStartInValueText = valueText.lastIndexOf("${", caretOffsetInValueText);
        if (exprStartInValueText == -1) return;

        int exprBodyStartInValueText = exprStartInValueText + 2;
        String typedPrefix;
        int dummyIdentifierPosInValueText = valueText.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER, exprBodyStartInValueText);

        if (dummyIdentifierPosInValueText != -1) {
            typedPrefix = valueText.substring(exprBodyStartInValueText, dummyIdentifierPosInValueText);
        } else {
            typedPrefix = valueText.substring(exprBodyStartInValueText, caretOffsetInValueText);
        }
        typedPrefix = typedPrefix.trim();

        String currentCompletionSegment;
        int lastDotIndexInTypedPrefix = typedPrefix.lastIndexOf('.');
        if (lastDotIndexInTypedPrefix != -1) {
            currentCompletionSegment = typedPrefix.substring(lastDotIndexInTypedPrefix + 1);
        } else {
            currentCompletionSegment = typedPrefix;
        }

        CompletionResultSet exprResult = result.withPrefixMatcher(currentCompletionSegment);

        if (StringUtil.isNotEmpty(typedPrefix) || dummyIdentifierPosInValueText != -1) {
            for (ModelAttributeInfo modelAttr : SpringModelAttributeUtil.getModelAttributes(project)) {
                Set<String> propertyChains = new TreeSet<>();
                collectPropertyChains(modelAttr.psiClass, modelAttr.name, propertyChains, 4);
                addFilteredCompletions(exprResult, propertyChains, typedPrefix, new DollarSignInsertHandler());
            }
        }
    }

    private static void collectPropertyChains(PsiClass psiClass, String currentPrefix, Set<String> result, int depth) {
        if (psiClass == null || depth <= 0) return;

        if (PROPERTY_CHAIN_CACHE.containsKey(psiClass)) {
            Set<String> cached = PROPERTY_CHAIN_CACHE.get(psiClass);
            for (String path : cached) {
                result.add(currentPrefix.isEmpty() ? path : currentPrefix + "." + path);
            }
            return;
        }

        Set<String> classResults = new TreeSet<>();
        for (PsiField field : psiClass.getAllFields()) {
            if (field.hasModifierProperty(PsiModifier.STATIC)) continue;

            String path = field.getName();
            classResults.add(path);

            PsiClass fieldType = PsiUtil.resolveClassInClassTypeOnly(field.getType());
            if (shouldRecurseIntoType(field.getType())) {
                collectPropertyChains(fieldType, path, classResults, depth - 1);
            }
        }

        PROPERTY_CHAIN_CACHE.put(psiClass, classResults);

        for (String path : classResults) {
            result.add(currentPrefix.isEmpty() ? path : currentPrefix + "." + path);
        }
    }

    private PsiClass findClassForExpression(String expression, Project project) {
        if (expression.startsWith("${") && expression.endsWith("}")) {
            String modelAttributeName = expression.substring(2, expression.length() - 1);
            for (ModelAttributeInfo attr : SpringModelAttributeUtil.getModelAttributes(project)) {
                if (attr.name.equals(modelAttributeName)) {
                    return attr.psiClass;
                }
            }
        } else {
            for (ModelAttributeInfo attr : SpringModelAttributeUtil.getModelAttributes(project)) {
                if (attr.name.equals(expression)) {
                    return attr.psiClass;
                }
            }
        }
        return null;
    }

    private String getThObjectExpression(PsiElement xmlTag) {
        if (xmlTag instanceof XmlTag) {
            XmlAttribute thObject = ((XmlTag) xmlTag).getAttribute("th:object");
            return thObject != null ? thObject.getValue() : null;
        }
        return null;
    }

    private String getThEachExpression(PsiElement xmlTag) {
        if (xmlTag instanceof XmlTag) {
            XmlAttribute thEach = ((XmlTag) xmlTag).getAttribute("th:each");
            if (thEach != null) {
                String eachValue = thEach.getValue();
                return eachValue != null ? eachValue.split(":")[1].trim() : null;
            }
        }
        return null;
    }

    private static boolean shouldRecurseIntoType(PsiType type) {
        if (type == null) return false;
        String canonicalText = type.getCanonicalText();
        return !canonicalText.startsWith("java.lang.") &&
                !canonicalText.startsWith("java.util.") &&
                !canonicalText.startsWith("kotlin.");
    }

    private void addFilteredCompletions(CompletionResultSet result,
                                        Set<String> propertyChains,
                                        String typedPrefix,
                                        InsertHandler<LookupElement> insertHandler) {

        boolean hasDotInPrefix = typedPrefix.contains(".");
        String prefixAfterLastDot = hasDotInPrefix ?
                typedPrefix.substring(typedPrefix.lastIndexOf('.') + 1) :
                typedPrefix;

        for (String propChain : propertyChains) {
            if (typedPrefix.isEmpty()) {
                addCompletionItem(result, propChain, insertHandler);
            } else if (hasDotInPrefix) {
                if (propChain.startsWith(typedPrefix.substring(0, typedPrefix.lastIndexOf('.') + 1))) {
                    addCompletionItem(result, propChain, insertHandler);
                }
            } else {
                if (!propChain.contains(".") && propChain.startsWith(prefixAfterLastDot)) {
                    addCompletionItem(result, propChain, insertHandler);
                }
            }
        }
    }

    private void addCompletionItem(CompletionResultSet result,
                                   String propChain,
                                   InsertHandler<LookupElement> insertHandler) {
        String stringToInsert = propChain.substring(propChain.lastIndexOf('.') + 1);
        LookupElement element = LookupElementBuilder.create(stringToInsert)
                .withPresentableText(propChain)
                .withTypeText(getTypeTextForChain(propChain), false)
                .withInsertHandler(insertHandler);

        element.putUserData(FULL_PROP_CHAIN_KEY, propChain);
        result.addElement(element);
    }

    private String getTypeTextForChain(String propChain) {
        return "Object";
    }
}
