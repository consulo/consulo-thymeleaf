package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression; // Adjust package as needed

public class DollarSignInsertHandler extends ThymeleafExpressionInsertHandler {

    @Override
    protected String getExpressionPrefix() {
        return "${";
    }
}