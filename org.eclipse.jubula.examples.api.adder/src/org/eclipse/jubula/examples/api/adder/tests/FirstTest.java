package org.eclipse.jubula.examples.api.adder.tests;

import org.eclipse.jubula.examples.api.adder.SimpleAdder;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.junit.Test;

public class FirstTest {
    @Test
    private void test1(SimpleAdder sa) {
        sa.getValue1().replaceText("1");
        sa.getValue2().replaceText("2");
        sa.getCalculate().click(1, InteractionMode.primary);
        sa.getResult().checkText("3", Operator.equals);
    }
}
