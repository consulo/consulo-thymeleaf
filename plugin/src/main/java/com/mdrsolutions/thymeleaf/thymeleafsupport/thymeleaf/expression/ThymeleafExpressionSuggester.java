package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression;

import java.util.*;

public class ThymeleafExpressionSuggester {
    // Map attribute names to preferred expression types
    private static final Map<String, List<ExpressionSuggestion>> attributeExpressionMap = Map.ofEntries(
            // Special-cased attributes with rich suggestions
            Map.entry("th:text", List.of(
                    new ExpressionSuggestion("${}", "Variable Expression"),
                    new ExpressionSuggestion("#{message.key}", "Message Expression"),
                    new ExpressionSuggestion("*{}", "Selection Expression")
            )),
            Map.entry("th:if", List.of(
                    new ExpressionSuggestion("${}", "Variable Expression"),
                    new ExpressionSuggestion("#{message.key}", "Message Expression")
            )),
            Map.entry("th:each", List.of(
                    new ExpressionSuggestion("${item : items}", "Variable Expression")
            )),
            Map.entry("th:value", List.of(
                    new ExpressionSuggestion("${}", "Variable Expression"),
                    new ExpressionSuggestion("*{}", "Selection Expression")
            )),
            Map.entry("th:attr", List.of(
                    new ExpressionSuggestion("${}", "Variable Expression"),
                    new ExpressionSuggestion("*{}", "Selection Expression"),
                    new ExpressionSuggestion("#{message.key}", "Message Expression"),
                    new ExpressionSuggestion("@{/path}", "Link URL Expression")
            )),
            Map.entry("th:attrappend", List.of(
                    new ExpressionSuggestion("${}", "Variable Expression"),
                    new ExpressionSuggestion("*{}", "Selection Expression")
            )),
            Map.entry("th:class", List.of(
                    new ExpressionSuggestion("${}", "Variable Expression"),
                    new ExpressionSuggestion("*{}", "Selection Expression")
            )),
            Map.entry("th:classappend", List.of(
                    new ExpressionSuggestion("${}", "Variable Expression"),
                    new ExpressionSuggestion("*{}", "Selection Expression")
            )),
            Map.entry("th:style", List.of(
                    new ExpressionSuggestion("${}", "Variable Expression"),
                    new ExpressionSuggestion("*{}", "Selection Expression")
            )),
            Map.entry("th:field", List.of(
                    new ExpressionSuggestion("*{field}", "Selection Expression")
            )),
            Map.entry("th:insert", List.of(
                    new ExpressionSuggestion("~{fragments/header}", "Fragment Expression")
            )),
            Map.entry("th:replace", List.of(
                    new ExpressionSuggestion("~{fragments/footer}", "Fragment Expression")
            )),
            Map.entry("th:include", List.of(
                    new ExpressionSuggestion("~{fragments/footer}", "Fragment Expression")
            )),
            Map.entry("th:remove", List.of(
                    new ExpressionSuggestion("${}", "Fragment Expression")
            )),
            Map.entry("th:href", List.of(
                    new ExpressionSuggestion("@{/path}", "Link URL Expression")
            )),
            Map.entry("th:action", List.of(
                    new ExpressionSuggestion("@{/path}", "Link URL Expression")
            )),
            Map.entry("th:src", List.of(
                    new ExpressionSuggestion("@{/path}", "URL Expression")
            )),

            // All other attributes get the default
            Map.entry("layout:fragment", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:abbr", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:accept", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:accept-charset", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:accesskey", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:align", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:alt", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:archive", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:audio", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:autocomplete", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:axis", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:background", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:bgcolor", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:border", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:cellpadding", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:cellspacing", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:challenge", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:charset", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:cite", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:classid", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:codebase", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:codetype", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:cols", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:colspan", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:compact", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:content", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:contenteditable", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:contextmenu", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:data", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:datetime", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:dir", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:draggable", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:dropzone", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:enctype", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:errors", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:errorclass", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:for", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:form", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:formaction", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:formenctype", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:formmethod", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:formtarget", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:frame", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:frameborder", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:headers", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:height", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:high", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:hreflang", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:hspace", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:http-equiv", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:icon", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:id", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:keytype", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:kind", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:label", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:lang", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:list", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:longdesc", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:low", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:manifest", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:marginheight", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:marginwidth", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:max", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:maxlength", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:media", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:method", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:min", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:name", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:optimum", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:pattern", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:placeholder", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:poster", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:preload", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:radiogroup", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:rel", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:rev", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:rows", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:rowspan", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:rules", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:sandbox", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:scheme", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:scope", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:scrolling", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:size", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:sizes", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:span", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:spellcheck", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:srclang", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:standby", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:start", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:step", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:summary", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:tabindex", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:target", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:title", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:type", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:usemap", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:valuetype", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:vspace", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:width", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:wrap", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:xmlbase", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:xmllang", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:xmlspace", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:with", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:fragment", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:case", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:switch", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:object", List.of(new ExpressionSuggestion("${}", "Variable Expression"))),
            Map.entry("th:utext", List.of(new ExpressionSuggestion("${}", "Variable Expression")))
            // No trailing comma!
    );


    public static List<ExpressionSuggestion> getSuggestionsForAttribute(String attributeName) {
        return attributeExpressionMap.getOrDefault(attributeName, List.of(
                new ExpressionSuggestion("${ }", "Variable Expression")
        ));
    }

    // Helper class to group a suggestion and its description/type
    public static class ExpressionSuggestion {
        public final String template;
        public final String type;
        public ExpressionSuggestion(String template, String type) {
            this.template = template;
            this.type = type;
        }
    }
}

