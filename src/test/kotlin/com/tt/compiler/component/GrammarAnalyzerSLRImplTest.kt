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
        LR0Automaton(TestGrammar2).apply {
            val state0 = get(
                lr0s(
                    "S' -> · S",
                    "S -> · B B",
                    "B -> · a B",
                    "B -> · b"
                )
            )!!
            val state1 = get(
                lr0s("S' -> S ·")
            )!!
            val state2 = get(
                lr0s(
                    "S -> B · B",
                    "B -> · a B",
                    "B -> · b"
                )
            )!!
            val state3 = get(
                lr0s(
                    "B -> a · B",
                    "B -> · a B",
                    "B -> · b"
                )
            )!!
            val state4 = get(
                lr0s("B -> b ·")
            )!!
            val state5 = get(
                lr0s("S -> B B ·")
            )!!
            val state6 = get(
                lr0s("B -> a B ·")
            )!!

            state0.apply {
                assertEquals(get(nt("S"))!!, state1)
                assertEquals(get(nt("B"))!!, state2)
                assertEquals(get(t("b"))!!, state4)
                assertEquals(get(t("a"))!!, state3)
            }

            state2.apply {
                assertEquals(get(nt("B"))!!, state5)
                assertEquals(get(t("b"))!!, state4)
                assertEquals(get(t("a"))!!, state3)
            }

            state3.apply {
                assertEquals(get(nt("B"))!!, state6)
                assertEquals(get(t("b"))!!, state4)
                assertEquals(get(t("a"))!!, state3)
            }

            assert(!state1.hasOut())
            assert(!state4.hasOut())
            assert(!state5.hasOut())
            assert(!state6.hasOut())
        }
    }
}