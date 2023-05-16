package com.tt.compiler.component

import com.tt.compiler.component.impl.GrammarAnalyzerLALRImpl
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
    fun testAnalyzeGrammar1() {
        val analyzer = GrammarAnalyzerLALRImpl(TestGrammar5)
        analyzer.parseTable.let(::println)
        analyzer.analyze("b a a").forEach { println(it) }
    }

    @Test
    fun testAnalyzeGrammar2() {
        // TODO Fix Bug
        val analyzer = GrammarAnalyzerLALRImpl(TestGrammar6)
        analyzer.parseTable.let(::println)
        analyzer.analyze("id = id").forEach { println(it) }
    }

    @Test
    fun testMergeIdenticalKernel1() {
        val extendedGrammar = TestGrammar5.toExtended()
        val firstSet = FirstSet.from(extendedGrammar)
        val automaton = LR1Automaton(extendedGrammar, firstSet)
        val mergedAutomaton = automaton.mergeIdenticalKernelItemSets()

        mergedAutomaton.states.forEach { state ->
            println(state.value)
            println(mergedAutomaton.itemSets[state.value])
        }
    }

    @Test
    fun testMergeIdenticalKernel2() {
        val extendedGrammar = TestGrammar6.toExtended()
        val firstSet = FirstSet.from(extendedGrammar)
        val automaton = LR1Automaton(extendedGrammar, firstSet)
        val mergedAutomaton = automaton.mergeIdenticalKernelItemSets()

        mergedAutomaton.states
    }
}
