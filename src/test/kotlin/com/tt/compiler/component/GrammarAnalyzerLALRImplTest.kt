package com.tt.compiler.component

import com.tt.compiler.grammar.FirstSet
import com.tt.compiler.grammar.lr.LR1Automaton
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
        val item = LR1Item.parse("S -> · T E LookAhead { $,a,b }")
        val item2 = LR1Item.parse("S -> · T E  LookAhead{ }")
        assertEquals("S -> · T E LookAhead { \$, a, b }", item.toExpression())
        assertEquals("S -> · T E LookAhead {  }", item2.toExpression())
    }

    @Test
    fun testBuildLR1Item() {
        val extendedGrammar = TestGrammar5.toExtended()
        val firstSet = FirstSet.from(extendedGrammar)
        val automaton = LR1Automaton(extendedGrammar, firstSet)
        automaton.itemSets.forEachIndexed { index, closure ->
            println("I$index:")
            closure.forEach { println(it) }
        }

        println("==========")
        automaton.mergeIdenticalKernelItemSets().itemSets.forEachIndexed { index, closure ->
            println("I$index:")
            closure.forEach { println(it) }
        }
    }
}