package com.tt.compiler.component

import com.tt.compiler.component.impl.GrammarAnalyzerSLRImpl
import com.tt.compiler.grammar.FirstSet
import com.tt.compiler.grammar.FollowSet
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.lr.Action
import com.tt.compiler.grammar.lr.LR0Automaton
import com.tt.compiler.grammar.lr.LR0Item
import com.tt.compiler.grammar.lr.SLRParseTable
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

            states[0].apply {
                assertEquals(get(nt("S"))!!, states[1])
                assertEquals(get(nt("B"))!!, states[2])
                assertEquals(get(t("b"))!!, states[4])
                assertEquals(get(t("a"))!!, states[3])
            }

            states[2].apply {
                assertEquals(get(nt("B"))!!, states[5])
                assertEquals(get(t("b"))!!, states[4])
                assertEquals(get(t("a"))!!, states[3])
            }

            states[3].apply {
                assertEquals(get(nt("B"))!!, states[6])
                assertEquals(get(t("b"))!!, states[4])
                assertEquals(get(t("a"))!!, states[3])
            }


            assert(sequenceOf(1, 4, 5, 6).all { !states[it].hasOut() })
        }
    }

    @Test
    fun testLR0Automaton3() {
        LR0Automaton(TestGrammar3).apply {
            assert(states.size == 12)
            states[0].apply {
                assertEquals(get(nt("S"))!!, states[1])
                assertEquals(get(nt("T"))!!, states[2])
                assertEquals(get(nt("F"))!!, states[3])
                assertEquals(get(t("("))!!, states[4])
                assertEquals(get(t("id"))!!, states[5])
            }

            states[1].apply {
                assertEquals(get(t("+"))!!, states[6])
            }

            states[2].apply {
                assertEquals(get(t("*"))!!, states[7])
            }

            states[4].apply {
                assertEquals(get(nt("T"))!!, states[2])
                assertEquals(get(nt("F"))!!, states[3])
                assertEquals(get(t("id"))!!, states[5])
                assertEquals(get(t("("))!!, states[4])
            }


            states[6].apply {
                assertEquals(get(nt("T"))!!, states[9])
                assertEquals(get(nt("F"))!!, states[3])
                assertEquals(get(t("id"))!!, states[5])
                assertEquals(get(t("("))!!, states[4])
            }

            states[7].apply {
                assertEquals(get(nt("F"))!!, states[10])
                assertEquals(get(t("id"))!!, states[5])
                assertEquals(get(t("("))!!, states[4])
            }


            states[8].apply {
                assertEquals(get(t(")"))!!, states[11])
                assertEquals(get(t("+"))!!, states[6])
            }


            states[9].apply {
                assertEquals(get(t("*"))!!, states[7])
            }

            assert(sequenceOf(3, 5, 10, 11).all { !states[it].hasOut() })
        }
    }

    @Test
    fun testSLRParseTable() {
        val grammar = TestGrammar3
        val firstSet = FirstSet.from(grammar)
        val followSet = FollowSet.from(firstSet, grammar)
        val automaton = LR0Automaton(grammar)
        val parseTable = SLRParseTable(automaton, followSet)

        parseTable.goto.apply {
            assertEquals(
                mapOf(
                    nt("S") to 1,
                    nt("T") to 2,
                    nt("F") to 3,
                ), this[0]
            )

            assertEquals(
                mapOf(
                    nt("S") to 8,
                    nt("T") to 2,
                    nt("F") to 3,
                ), this[4]
            )

            assertEquals(
                mapOf(
                    nt("T") to 9,
                    nt("F") to 3,
                ), this[6]
            )

            assertEquals(
                mapOf(
                    nt("F") to 10,
                ), this[7]
            )

            sequenceOf(1, 2, 3, 5, 8, 9, 10, 11).forEach {
                assertEquals(
                    mapOf(),
                    this[it]
                )
            }
        }

        parseTable.action.apply {
            assertEquals(
                mapOf(
                    t("(") to Action.Shift(4),
                    t("id") to Action.Shift(5),
                ), this[0]
            )

            assertEquals(
                mapOf(
                    t("+") to Action.Shift(6),
                    t("$") to Action.Accept,
                ), this[1]
            )
        }

        println(parseTable)
    }

    @Test
    fun testSLRParse1() {
        val analyzer = GrammarAnalyzerSLRImpl(TestGrammar3)
        val productions = analyzer.analyze("id + id * id")
        productions.forEach { println(it) }
    }

    @Test
    fun testSLRParse2() {
        val parser = GrammarAnalyzerSLRImpl(TestGrammar2)

        assertEquals(
            listOf(
                "B -> b",
                "B -> b",
                "B -> a B",
                "S -> B B"
            ).map { p(it) },
            parser.analyze("b a b")
        )
    }
}