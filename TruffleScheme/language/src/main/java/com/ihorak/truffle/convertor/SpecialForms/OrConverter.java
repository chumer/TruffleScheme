package com.ihorak.truffle.convertor.SpecialForms;

import com.ihorak.truffle.convertor.ListToExpressionConverter;
import com.ihorak.truffle.convertor.context.ParsingContext;
import com.ihorak.truffle.node.SchemeExpression;
import com.ihorak.truffle.node.exprs.builtin.arithmetic.OneArgumentExprNodeGen;
import com.ihorak.truffle.node.literals.BooleanLiteralNode;
import com.ihorak.truffle.node.special_form.OrExprNode;
import com.ihorak.truffle.type.SchemeCell;

import java.util.ArrayList;
import java.util.List;

public class OrConverter {

    private OrConverter() {}

    public static SchemeExpression convert(SchemeCell orList, ParsingContext context) {
        var schemeExprs = convertSchemeCellToSchemeExpressions(orList.cdr, context);
        if (schemeExprs.isEmpty()) return new BooleanLiteralNode(false);
        if (schemeExprs.size() == 1) return OneArgumentExprNodeGen.create(schemeExprs.get(0));
        return reduceOr(schemeExprs);
    }



    private static OrExprNode reduceOr(List<SchemeExpression> arguments) {
        if (arguments.size() > 2) {
            return new OrExprNode(arguments.remove(0), reduceOr(arguments));
        } else {
            return new OrExprNode(arguments.get(0), arguments.get(1));
        }
    }

    private static List<SchemeExpression> convertSchemeCellToSchemeExpressions(SchemeCell schemeCell, ParsingContext context) {
        List<SchemeExpression> result = new ArrayList<>();
        for (Object obj : schemeCell) {
            result.add(ListToExpressionConverter.convert(obj, context));
        }

        return result;
    }
}
