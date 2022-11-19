package com.ihorak.truffle.convertor.util;

import com.ihorak.truffle.type.SchemeSymbol;

public class SpecialFormUtils {

    public static boolean isSpecialForm(SchemeSymbol symbol) {
        switch (symbol.getValue()) {
            case "if":
            case "lambda":
            case "define":
            case "quote":
            case "quasiquote":
            case "let":
            case "let*":
            case "and":
            case "or":
            case "cond":
            case "letrec":
                return true;
            default:
                return false;
        }
    }
}
