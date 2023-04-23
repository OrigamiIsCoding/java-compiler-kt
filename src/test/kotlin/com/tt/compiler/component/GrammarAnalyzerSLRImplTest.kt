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
    fun testLR0Automaton2() {
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

    @Test
    fun testLR0Automaton3() {
        LR0Automaton(TestGrammar3).apply {
            val state0 = get(
                lr0s(
                    "S' -> · S",
                    "S -> · S + T",
                    "S -> · T",
                    "T -> · T * F",
                    "T -> · F",
                    "F -> · ( S )",
                    "F -> · id"
                )
            )!!
            val state1 = get(
                lr0s(
                    "S' -> S ·",
                    "S -> S · + T"
                )
            )!!
            val state2 = get(
                lr0s(
                    "S -> T ·",
                    "T -> T · * F",
                )
            )!!
            val state3 = get(
                lr0s("T -> F ·")
            )!!
            val state4 = get(
                lr0s(
                    "F -> ( · S )",
                    "S -> · S + T",
                    "S -> · T",
                    "T -> · T * F",
                    "T -> · F",
                    "F -> · ( S )",
                    "F -> · id"
                )
            )!!
            val state5 = get(
                lr0s("F -> id ·")
            )!!
            val state6 = get(
                lr0s(
                    "S -> S + · T",
                    "T -> · T * F",
                    "T -> · F",
                    "F -> · ( S )",
                    "F -> · id"
                )
            )!!
            val state7 = get(
                lr0s(
                    "T -> T * · F",
                    "F -> · ( S )",
                    "F -> · id"
                )
            )!!
            val state8 = get(
                lr0s(
                    "F -> ( S · )",
                    "S -> S · + T",
                )
            )!!
            val state9 = get(
                lr0s(
                    "S -> S + T ·",
                    "T -> T · * F",
                )
            )!!
            val state10 = get(
                lr0s("T -> T * F ·")
            )!!
            val state11 = get(
                lr0s("F -> ( S ) ·")
            )!!

            state0.apply {
                assertEquals(get(nt("S"))!!, state1)
                assertEquals(get(nt("T"))!!, state2)
                assertEquals(get(nt("F"))!!, state3)
                assertEquals(get(t("("))!!, state4)
                assertEquals(get(t("id"))!!, state5)
            }

            state1.apply {
                assertEquals(get(t("+"))!!, state6)
            }

            state2.apply {
                assertEquals(get(t("*"))!!, state7)
            }

            state4.apply {
                assertEquals(get(nt("T"))!!, state2)
                assertEquals(get(nt("F"))!!, state3)
                assertEquals(get(t("id"))!!, state5)
                assertEquals(get(t("("))!!, state4)
            }

            state6.apply {
                assertEquals(get(nt("T"))!!, state9)
                assertEquals(get(nt("F"))!!, state3)
                assertEquals(get(t("id"))!!, state5)
                assertEquals(get(t("("))!!, state4)
            }

            state7.apply {
                assertEquals(get(nt("F"))!!, state10)
                assertEquals(get(t("id"))!!, state5)
                assertEquals(get(t("("))!!, state4)
            }

            state8.apply {
                assertEquals(get(t(")"))!!, state11)
                assertEquals(get(t("+"))!!, state6)
            }

            state9.apply {
                assertEquals(get(t("*"))!!, state7)
            }

            assert(!state3.hasOut())
            assert(!state5.hasOut())
            assert(!state10.hasOut())
            assert(!state11.hasOut())
        }
    }
}