package com.tt.compiler.component

import com.tt.compiler.grammar.FirstSet
import com.tt.compiler.grammar.lr.LR1Item
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Origami
 * @date 5/6/2023 8:56 AM
 */
class GrammarAnalyzerLALRImplTest {
    @Test
    fun testParseLR1Item() {
        val item = LR1Item.parse("S -> · T E , { $,a,b }")
        val item2 = LR1Item.parse("S -> · T E , { }")
        assertEquals("S -> · T E , { \$, a, b }", item.toExpression())
        assertEquals("S -> · T E , {  }", item2.toExpression())
    }

    @Test
    fun testBuildLR1Item() {
        println(FirstSet.from(TestGrammar4))
    }
}