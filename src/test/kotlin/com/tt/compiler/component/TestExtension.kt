package com.tt.compiler.component

import com.tt.compiler.grammar.Grammar
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol
import com.tt.compiler.grammar.lr.LR0Item

/**
 * @author Origami
 * @date 4/23/2023 5:49 PM
 */
val TestGrammar1 = """
    S  -> T S'
    S' -> + T S' | ε
    T  -> F T'
    T' -> * F T' | ε
    F  -> ( S ) | id
""".trimIndent().parse()

val TestGrammar2 = """
    S -> B B
    B -> a B
    B -> b
""".trimIndent().parse()

val TestGrammar3 = """
    S -> S + T
    S -> T
    T -> T * F
    T -> F
    F -> ( S )
    F -> id
""".trimIndent().parse()

val TestGrammar4 = """
    S -> C C
    C -> c C | d
""".trimIndent().parse()

val TestGrammar5 = """
    S -> B B
    B -> b B
    B -> a
""".trimIndent().parse()

fun s(value: String) = Symbol.from(value)
fun t(value: String) = Symbol.terminal(value)
fun nt(value: String) = Symbol.nonTerminal(value)
fun p(value: String) = Production.parse(value).first()
fun lr0(value: String): LR0Item = LR0Item.parse(value)
fun lr0s(vararg values: String) = values.map { lr0(it) }.toSet()

fun String.parse() = Grammar.parse(this)
