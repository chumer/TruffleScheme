package com.ihorak.truffle.node.special_form;

import com.ihorak.truffle.node.SchemeExpression;
import com.ihorak.truffle.node.callable.DispatchNodeGen;
import com.ihorak.truffle.type.SchemeSymbol;
import com.ihorak.truffle.type.UserDefinedProcedure;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

public class LambdaExprNode extends SchemeExpression {


    private CallTarget callTarget;
    private int amountOfArguments;
    private boolean hasOptionalArgs;

    public LambdaExprNode(CallTarget callTarget, int amountOfArguments, boolean hasOptionalArgs) {
        this.callTarget = callTarget;
        this.amountOfArguments = amountOfArguments;
        this.hasOptionalArgs = hasOptionalArgs;
    }

    @Override
    public UserDefinedProcedure executeUserDefinedProcedure(VirtualFrame virtualFrame) {
        return new UserDefinedProcedure(callTarget, amountOfArguments, hasOptionalArgs, virtualFrame.materialize());
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        return executeUserDefinedProcedure(virtualFrame);
    }
}
