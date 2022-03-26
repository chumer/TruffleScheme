package com.ihorak.truffle.node.exprs.builtin.list;

import com.ihorak.truffle.exceptions.SchemeException;
import com.ihorak.truffle.node.SchemeExpression;
import com.ihorak.truffle.type.SchemeCell;
import com.ihorak.truffle.type.SchemePair;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.BranchProfile;

@NodeChild(value = "list")
public abstract class CdrExprNode extends SchemeExpression {

    private final BranchProfile emptyListProfile = BranchProfile.create();

    @Specialization
    protected Object doPair(SchemePair pair) {
        return pair.getSecond();
    }

    @Specialization
    protected SchemeCell doList(SchemeCell list) {
        if (list == SchemeCell.EMPTY_LIST) {
            emptyListProfile.enter();
            throw new SchemeException("cdr: contract violation\nexpected: pair? or list?\ngiven: " + list, this);
        }
        return list.cdr;
    }

    @Fallback
    protected Object fallback(Object value) {
        throw new SchemeException("cdr: contract violation\nexpected: pair? or list?\ngiven: " + value, this);
    }
}
