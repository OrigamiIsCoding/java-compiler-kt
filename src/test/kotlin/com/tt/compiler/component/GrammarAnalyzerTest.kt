package com.tt.compiler.component

import com.tt.compiler.grammar.FirstSet
import com.tt.compiler.grammar.FollowSet
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Origami
 * @date 4/19/2023 5:19 PM
 */
class GrammarAnalyzerTest {

    @BeforeTest
    fun setUp() {
    }

    @Test
    fun testProductionParse() {
        assertEquals(
            Production(
                left = s("S"),
                right = listOf(
                    s("T"), s("S'")
                )
            ),
            Production.parse("S  -> T S'").first()
        )

        assertEquals(
            listOf(
                Production(
                    left = s("S'"),
                    right = listOf(s("+"), s("T"), s("S'"))
                ),
                Production(
                    left = s("S'"),
                    right = listOf(s("ε"))
                )
            ),
            Production.parse("S' -> + T S' | ε")
        )
    }

    @Test
    fun testFirstSet() {
        val productions = grammar(
            """
            S  -> T S'
            S' -> + T S' | ε
            T  -> F T'
            T' -> * F T' | ε
            F  -> ( S ) | id
        """.trimIndent()
        )

        val firstSet = FirstSet(productions)
        assertEquals(mapOf(
            s("S") to setOf(s("id"), s("(")),
            s("S'") to setOf(s("+"), s("ε")),
            s("T") to setOf(s("id"), s("(")),
            s("T'") to setOf(s("*"), s("ε")),
            s("F") to setOf(s("id"), s("("))
        ), firstSet.mapValues { it -> it.value.map { it.first }.toSet() })
    }

    @Test
    fun testFollowSet() {
        val productions = grammar(
            """
            S  -> T S'
            S' -> + T S' | ε
            T  -> F T'
            T' -> * F T' | ε
            F  -> ( S ) | id
        """.trimIndent()
        )

        val firstSet = FirstSet(productions)
        val followSet = FollowSet(firstSet, productions)
        assertEquals(
            mapOf(
                s("S") to setOf(s("$"), s(")")),
                s("S'") to setOf(s("$"), s(")")),
                s("T") to setOf(s("$"), s("+"), s(")")),
                s("T'") to setOf(s("$"), s("+"), s(")")),
                s("F") to setOf(s("$"), s("+"), s("*"), s(")"))
            ), followSet
        )
    }
}

fun s(value: String) = Symbol.from(value)
fun grammar(text: String) = text.split("\n").flatMap(Production::parse)