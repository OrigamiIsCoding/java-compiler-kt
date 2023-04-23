package com.tt.compiler.component

import com.tt.compiler.component.impl.GrammarAnalyzerLL1Impl
import com.tt.compiler.grammar.*
import com.tt.compiler.grammar.ll.LL1ParseTable
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Origami
 * @date 4/19/2023 5:19 PM
 */
class GrammarAnalyzerLL1ImplTest {

    @BeforeTest
    fun setUp() {
    }

    @Test
    fun testProductionParse() {
        assertEquals(
            Production(
                left = nt("S"),
                right = listOf(
                    s("T"), s("S'")
                )
            ),
            Production.parse("S  -> T S'").first()
        )

        assertEquals(
            listOf(
                Production(
                    left = nt("S'"),
                    right = listOf(s("+"), s("T"), s("S'"))
                ),
                Production(
                    left = nt("S'"),
                    right = listOf(s("ε"))
                )
            ),
            Production.parse("S' -> + T S' | ε")
        )
    }

    @Test
    fun testFirstSet() {
        val productions = TestGrammar1

        val firstSet = FirstSet.from(productions)
        assertEquals(mapOf(
            nt("S") to setOf(s("id"), s("(")),
            nt("S'") to setOf(s("+"), s("ε")),
            nt("T") to setOf(s("id"), s("(")),
            nt("T'") to setOf(s("*"), s("ε")),
            nt("F") to setOf(s("id"), s("("))
        ), firstSet.mapValues { it -> it.value.map { it.first }.toSet() })
    }

    @Test
    fun testFollowSet() {
        val productions = TestGrammar1

        val firstSet = FirstSet.from(productions)
        val followSet = FollowSet.from(firstSet, productions)
        assertEquals(
            mapOf(
                nt("S") to setOf(s("$"), s(")")),
                nt("S'") to setOf(s("$"), s(")")),
                nt("T") to setOf(s("$"), s("+"), s(")")),
                nt("T'") to setOf(s("$"), s("+"), s(")")),
                nt("F") to setOf(s("$"), s("+"), s("*"), s(")"))
            ), followSet
        )
    }

    @Test
    fun testLL1ParseTable() {
        val productions = TestGrammar1

        val firstSet = FirstSet.from(productions)
        val followSet = FollowSet.from(firstSet, productions)
        val table = LL1ParseTable(firstSet, followSet)

        assertEquals(
            mapOf(
                t("*") to p("T' -> * F T'"),
                t("$") to p("T' -> ε"),
                t("+") to p("T' -> ε"),
                t(")") to p("T' -> ε"),
            ), table[nt("T'")]!!
        )
        assertEquals(
            mapOf(
                t("(") to p("S -> T S'"),
                t("id") to p("S -> T S'"),
            ), table[nt("S")]!!
        )
        assertEquals(
            mapOf(
                t("$") to p("S' -> ε"),
                t("+") to p("S' -> + T S'"),
                t(")") to p("S' -> ε"),
            ), table[nt("S'")]!!
        )
        assertEquals(
            mapOf(
                t("(") to p("T -> F T'"),
                t("id") to p("T -> F T'"),
            ), table[nt("T")]!!
        )
        assertEquals(
            mapOf(
                t("(") to p("F -> ( S )"),
                t("id") to p("F -> id"),
            ), table[nt("F")]!!
        )
    }

    @Test
    fun testSentenceParse() {
        val grammarAnalyzerLL1Impl = GrammarAnalyzerLL1Impl(TestGrammar1)
        val productions = grammarAnalyzerLL1Impl.analyze("id + id * id")
        assertEquals(
            listOf(
                "S -> T S'",
                "T -> F T'",
                "F -> id",
                "T' -> ε",
                "S' -> + T S'",
                "T -> F T'",
                "F -> id",
                "T' -> * F T'",
                "F -> id",
                "T' -> ε",
                "S' -> ε",
            ).map { Production.parse(it).first() },
            productions
        )
    }
}