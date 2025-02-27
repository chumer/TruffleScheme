package com.ihorak.truffle.macro;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class DefineMacroExprNodeTest {

    private Context context;

    @Before
    public void setUp() {
        context = Context.create();
    }


    @Test
    public void givenDefineMacro_whenEvaluated_thenUndefinedShouldBeReturned() {
        var program = "(define-macro macro (lambda (test first) `(if ,test ,first #f)))";

        var result = context.eval("scm", program);

        assertEquals(true, result.isNull());
        assertEquals("undefined", result.toString());
    }

    @Test
    public void givenDefineMacro_whenCalled_thenCorrectResultShouldBeReturned() {
        var program = "(define-macro macro (lambda (test first) `(if ,test ,first #f))) (macro (= 10 5) 5)";

        var result = context.eval("scm", program);

        assertEquals(false, result.asBoolean());
    }


    @Test
    public void givenDefineMacroWithUnknownValue_whenEvaluated_thenSchemeMacroShouldBeReturned() {
        var program = "(define-macro macro (lambda (test first) (list 'if ivo first #f))) (macro #f 5)";

        var result = assertThrows(PolyglotException.class, () -> context.eval("scm", program)).getMessage();

        assertEquals("""
                             'ivo: undefined
                             cannot reference an identifier before its definition""", result);
    }
}
