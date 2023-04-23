package com.tt.compiler.component

import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.lr.LR0Automaton
import com.tt.compiler.grammar.lr.LR0Item
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Origami
 * @date 4/22/2023 7:55 PM
 */
class GrammarAnalyzerSLRImplTest {
    @Test
    fun testLR0Item() {
        val production = Production.parse("S -> T E").first()
        val lr0Item = LR0Item(production)
        assertEquals("S -> · T E", lr0Item.toExpression())
        assertEquals("S -> T · E", lr0Item.next().toExpression())
        assertEquals("S -> T E ·", lr0Item.next().next().toExpression())
        assertThrows<NoSuchElementException> {
            lr0Item.next().next().next()
        }
    }

    @Test
    fun testLR0Automata() {
        val automata = LR0Automaton(TestGrammar2)
        val state0 = automata[listOf(
            "S' -> · S",
            "S -> · B B",
            "B -> · a B",
            "B -> · b"
        ).map(::lr0).toSet()]!!
        val state1 = automata[setOf(
            lr0("S' -> S ·")
        )]!!
        val state2 = automata[listOf(
            "S -> B · B",
            "B -> · a B",
            "B -> · b"
        ).map(::lr0).toSet()]!!
        val state3 = automata[listOf(
            "B -> a · B",
            "B -> · a B",
            "B -> · b"
        ).map(::lr0).toSet()]!!
        val state4 = automata[setOf(
            lr0("B -> b ·")
        )]!!
        val state5 = automata[setOf(
            lr0("S -> B B ·")
        )]!!
        val state6 = automata[setOf(
            lr0("B -> a B ·")
        )]!!

        assertEquals(state0[nt("S")]!!, state1)
        assertEquals(state0[nt("B")]!!, state2)
        assertEquals(state0[t("b")]!!, state4)
        assertEquals(state0[t("a")]!!, state3)

        assertEquals(state2[nt("B")]!!, state5)
        assertEquals(state2[t("b")]!!, state4)
        assertEquals(state2[t("a")]!!, state3)


        assertEquals(state3[nt("B")]!!, state6)
        assertEquals(state3[t("b")]!!, state4)
        assertEquals(state3[t("a")]!!, state3)

        assert(!state1.hasOut())
        assert(!state4.hasOut())
        assert(!state5.hasOut())
        assert(!state6.hasOut())
    }
}