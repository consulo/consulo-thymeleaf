package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression;

public class AsteriskInsertHandler extends ThymeleafExpressionInsertHandler {

    @Override
    protected String getExpressionPrefix() {
        return "*{";
    }
}