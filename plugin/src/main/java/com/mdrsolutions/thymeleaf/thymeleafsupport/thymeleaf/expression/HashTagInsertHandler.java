package com.mdrsolutions.thymeleaf.thymeleafsupport.thymeleaf.expression;

public class HashTagInsertHandler extends ThymeleafExpressionInsertHandler  {

    @Override
    protected String getExpressionPrefix() {
        return "#{";
    }
}
