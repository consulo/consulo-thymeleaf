package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.mdrsolutions.thymeleaf.thymeleafsupport.spring.attributes.ModelAttributeInfo;
import com.mdrsolutions.thymeleaf.thymeleafsupport.spring.attributes.SpringModelAttributeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;

public class ThymeleafExpressionCompletionContributor extends CompletionContributor {

    public static final com.intellij.openapi.util.Key<String> FULL_PROP_CHAIN_KEY =
            com.intellij.openapi.util.Key.create("FULL_PROP_CHAIN");
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
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        PsiElement parent = position.getParent();

                        if (!(parent instanceof XmlAttributeValue)) return;

                        PsiElement grandparent = parent.getParent();
                        if (!(grandparent instanceof XmlAttribute xmlAttribute)) return;

                        String attributeName = xmlAttribute.getName();
                        Project project = parameters.getEditor().getProject();

                        if (project == null) return;

                        // Basic suggestions ${}, #{}, *{}
                        for (ThymeleafExpressionSuggester.ExpressionSuggestion suggestion :
                                ThymeleafExpressionSuggester.getSuggestionsForAttribute(attributeName)) {
                            System.out.println("Basic suggestions: " + suggestion.template);
                            result.addElement(
                                    LookupElementBuilder.create(suggestion.template)
                                            .withPresentableText(suggestion.template)
                                            .withTypeText(suggestion.type, true)
                            );
                        }

                        // Inside the double quotes
                        String valueText = ((XmlAttributeValue) parent).getValue();

                        // --- Determine Expression Type and Apply Specific Logic ---
                        if (valueText.contains("${")) { // This block handles ${} expressions
                            handleDollarSignExpression(parameters, parent, result, project, valueText);
                        } else if (valueText.contains("*{")) { // <--- Add this block for Asterisk expressions
                            handleAsteriskExpression(parameters, parent, result, project, valueText);
                        }
                    }
                }
        );
    }

    private void handleAsteriskExpression(@NotNull CompletionParameters parameters,
                                          @NotNull PsiElement parent,
                                          @NotNull CompletionResultSet result,
                                          @NotNull Project project,
                                          @NotNull String valueText) {
        // Get the absolute offset of the start of the XML attribute's *value* (excluding quotes)
        int attributeValueStartOffsetInFile = ((XmlAttributeValue) parent).getValueTextRange().getStartOffset();

        // The caret's position relative to the start of the *valueText* string
        int caretOffsetInValueText = parameters.getOffset() - attributeValueStartOffsetInFile;

        // Find the starting index of `*{` within the valueText, searching backwards from caret
        int exprStartInValueText = valueText.lastIndexOf("*{", caretOffsetInValueText);
        if (exprStartInValueText == -1) {
            return; // Not currently in a *{} expression
        }

        int exprBodyStartInValueText = exprStartInValueText + 2; // Index after "*{ "

        String typedPrefix; // This is what the user has typed so far within the expression body
        int dummyIdentifierPosInValueText = valueText.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER, exprBodyStartInValueText);

        if (dummyIdentifierPosInValueText != -1) {
            typedPrefix = valueText.substring(exprBodyStartInValueText, dummyIdentifierPosInValueText);
        } else {
            typedPrefix = valueText.substring(exprBodyStartInValueText, caretOffsetInValueText);
        }
        typedPrefix = typedPrefix.trim(); // Important for matching

        // Determine the part of the typedPrefix that is the *current segment* for completion.
        String currentCompletionSegment;
        int lastDotIndexInTypedPrefix = typedPrefix.lastIndexOf('.');
        if (lastDotIndexInTypedPrefix != -1) {
            currentCompletionSegment = typedPrefix.substring(lastDotIndexInTypedPrefix + 1);
        } else {
            currentCompletionSegment = typedPrefix;
        }

        // Create a new CompletionResultSet with the current segment as the prefix matcher
        CompletionResultSet exprResult = result.withPrefixMatcher(currentCompletionSegment);

        // Get the th:object context
        PsiElement xmlTag = parent.getParent().getParent();
        String selectionExpression = getThObjectExpression(xmlTag);

        if (selectionExpression == null) {
            // Check for th:each context if no th:object
            selectionExpression = getThEachExpression(xmlTag);
        }

        if (selectionExpression != null) {
            // Find the PsiClass for the selection expression
            PsiClass selectionClass = findClassForExpression(selectionExpression, project);

            if (selectionClass != null) {
                Set<String> propertyChains = new TreeSet<>();
                collectPropertyChains(selectionClass, "", propertyChains, 4);

                // Use the new filtered completion method
                addFilteredCompletions(exprResult, propertyChains, typedPrefix, new AsteriskInsertHandler());
            }
        }
    }

    // --- NEW HELPER METHOD FOR ${} EXPRESSIONS ---
    private void handleDollarSignExpression(@NotNull CompletionParameters parameters,
                                            @NotNull PsiElement parent,
                                            @NotNull CompletionResultSet result,
                                            @NotNull Project project,
                                            @NotNull String valueText) {
        // Keep all this offset calculation logic
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

        // REPLACE JUST THIS SECTION AS SHOWN ABOVE
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

        // Check cache first
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

        // Cache results for this class
        PROPERTY_CHAIN_CACHE.put(psiClass, classResults);

        // Add to final results with proper prefix
        for (String path : classResults) {
            result.add(currentPrefix.isEmpty() ? path : currentPrefix + "." + path);
        }
    }

    private PsiClass findClassForExpression(String expression, Project project) {
        // Handle ${expression} patterns
        if (expression.startsWith("${") && expression.endsWith("}")) {
            String modelAttributeName = expression.substring(2, expression.length() - 1);
            for (ModelAttributeInfo attr : SpringModelAttributeUtil.getModelAttributes(project)) {
                if (attr.name.equals(modelAttributeName)) {
                    return attr.psiClass;
                }
            }
        }
        // Handle direct model attribute names
        else {
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
                // Extract the type part from "item : ${items}" pattern
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
            // If nothing typed yet, show all relevant completions
            if (typedPrefix.isEmpty()) {
                addCompletionItem(result, propChain, insertHandler);
            }
            // If we have a dot in prefix, filter chains starting with prefix
            else if (hasDotInPrefix) {
                if (propChain.startsWith(typedPrefix.substring(0, typedPrefix.lastIndexOf('.') + 1))) {
                    addCompletionItem(result, propChain, insertHandler);
                }
            }
            // Otherwise filter top-level properties starting with prefix
            else {
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
        // Implement logic to determine the type of the property chain
        // Could use PSI to find the actual type
        return "Object";
    }
}