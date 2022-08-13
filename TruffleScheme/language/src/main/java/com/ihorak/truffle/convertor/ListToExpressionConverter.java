package com.ihorak.truffle.convertor;

import com.ihorak.truffle.convertor.context.ParsingContext;
import com.ihorak.truffle.convertor.context.FrameIndexResult;
import com.ihorak.truffle.convertor.util.MacroUtils;
import com.ihorak.truffle.node.SchemeExpression;
import com.ihorak.truffle.node.literals.*;
import com.ihorak.truffle.node.scope.ReadClosureVariableExprNodeGen;
import com.ihorak.truffle.node.scope.ReadGlobalVariableExprNodeGen;
import com.ihorak.truffle.node.scope.ReadLocalVariableExprNodeGen;
import com.ihorak.truffle.convertor.util.SpecialFormUtils;
import com.ihorak.truffle.type.*;

import java.math.BigInteger;

public class ListToExpressionConverter {


    public static SchemeExpression convert(Object obj, ParsingContext context) {
        if (obj instanceof Long) {
            return convert((long) obj);
        } else if (obj instanceof SchemeSymbol) {
            return convert((SchemeSymbol) obj, context);
        } else if (obj instanceof Boolean) {
            return convert((boolean) obj);
        } else if (obj instanceof SchemeCell) {
            return convert((SchemeCell) obj, context);
        } else if (obj instanceof BigInteger) {
            return convert((BigInteger) obj);
        } else if (obj instanceof Double) {
            return convert((double) obj);
        } else {
            throw new IllegalArgumentException("ListToExpressionConverter: Unexpected type during conversion. Type: " + obj);

        }
    }


    private static SchemeExpression convert(long value) {
        return new LongLiteralNode(value);
    }

    private static SchemeExpression convert(BigInteger bigInteger) {
        return new BigIntLiteralNode(bigInteger);
    }

    private static SchemeExpression convert(double value) {
        return new DoubleLiteralNode(value);
    }

    private static SchemeExpression convert(boolean bool) {
        return new BooleanLiteralNode(bool);
    }

    private static SchemeExpression convert(SchemeSymbol symbol, ParsingContext context) {
        var indexPair = context.findClosureSymbol(symbol);
        if (indexPair != null) {
            return createReadLocalVariable(indexPair, symbol);
        } else {
            return ReadGlobalVariableExprNodeGen.create(symbol);
        }
    }

    private static SchemeExpression createReadLocalVariable(FrameIndexResult indexFrameIndexResult, SchemeSymbol symbol) {
        if (indexFrameIndexResult.getLexicalScopeDepth() == 0) {
            return ReadLocalVariableExprNodeGen.create(indexFrameIndexResult.getFrameIndex(), symbol);
        }
        return ReadClosureVariableExprNodeGen.create(indexFrameIndexResult.getLexicalScopeDepth(), indexFrameIndexResult.getFrameIndex(), symbol);
    }


    private static SchemeExpression convert(SchemeCell list, ParsingContext context) {
        var firstElement = list.car;

        if (isSpecialForm(firstElement)) {
            return SpecialFormConverter.convertListToSpecialForm(list, context);
        } else if (isMacro(firstElement)) {
            return SchemeMacroConverter.convertMarco(list, context);
        } else if (isLispMacro(firstElement)) {
            return LispMacroConverter.convertMacro(list, context);
        } else if (MacroUtils.isMacroDefined(firstElement)) {
            return null;
        } else {
            return ProcedureCallConverter.convertListToProcedureCall(list, context);
        }
    }

    private static boolean isSpecialForm(Object firstElementOfList) {
        return firstElementOfList instanceof SchemeSymbol && SpecialFormUtils.isSpecialForm((SchemeSymbol) firstElementOfList);
    }

    private static boolean isMacro(Object firstElementOfList) {
        return firstElementOfList instanceof SchemeSymbol && firstElementOfList.equals(new SchemeSymbol("define-macro"));
    }

    private static boolean isLispMacro(Object firstElementOfList) {
        return firstElementOfList instanceof SchemeSymbol && firstElementOfList.equals(new SchemeSymbol("defmacro"));
    }
}
