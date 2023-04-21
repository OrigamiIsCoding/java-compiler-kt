package com.tt.compiler.component

import com.tt.compiler.grammar.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

val TestGrammar1 = """
    S  -> T S'
    S' -> + T S' | ε
    T  -> F T'
    T' -> * F T' | ε
    F  -> ( S ) | id
""".trimIndent()

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
        val productions = TestGrammar1.toProductions()

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
        val productions = TestGrammar1.toProductions()

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

    @Test
    fun testLLOneParsingTable() {
        val productions = TestGrammar1.toProductions()

        val firstSet = FirstSet(productions)
        val followSet = FollowSet(firstSet, productions)
        val table = LLOneParsingTable(firstSet, followSet)
        assertEquals(
            """
            |              | (            | id           | *            | ${'$'}            | +            | )            | (            | id           | +            | ${'$'}            | )            | (            | id           |
            | F            | F -> ( S )   | F -> id      | null         | null         | null         | null         | F -> ( S )   | F -> id      | null         | null         | null         | F -> ( S )   | F -> id      |
            | T'           | null         | null         | T' -> * F T' | T' -> ε      | T' -> ε      | T' -> ε      | null         | null         | T' -> ε      | T' -> ε      | T' -> ε      | null         | null         |
            | T            | T -> F T'    | T -> F T'    | null         | null         | null         | null         | T -> F T'    | T -> F T'    | null         | null         | null         | T -> F T'    | T -> F T'    |
            | S'           | null         | null         | null         | S' -> ε      | S' -> + T S' | S' -> ε      | null         | null         | S' -> + T S' | S' -> ε      | S' -> ε      | null         | null         |
            | S            | S -> T S'    | S -> T S'    | null         | null         | null         | null         | S -> T S'    | S -> T S'    | null         | null         | null         | S -> T S'    | S -> T S'    |
        """.trimIndent(), table.toString()
        )
    }

    @Test
    fun testSentenceParse() {
        val grammarAnalyzer = GrammarAnalyzer(TestGrammar1.split("\n"))
        val productions = grammarAnalyzer.parse("id + id * id")
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

fun s(value: String) = Symbol.from(value)
fun String.toProductions() = this.split("\n").flatMap(Production::parse)