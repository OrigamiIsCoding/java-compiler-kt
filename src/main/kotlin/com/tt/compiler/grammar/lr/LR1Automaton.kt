package com.tt.compiler.grammar.lr

import com.tt.compiler.automata.Automaton
import com.tt.compiler.automata.Node
import com.tt.compiler.grammar.Grammar
import com.tt.compiler.grammar.Symbol

/**
 * @author Origami
 * @date 5/6/2023 9:34 AM
 */

private typealias ClosureOfItem1Sets = Set<LR1Item>

class LR1Automaton(
    grammar: Grammar
) : Automaton<Int, Symbol> {
    override val start: Node<Int, Symbol> = TODO()
    override val states: List<Node<Int, Symbol>> = TODO()
    val closures: List<ClosureOfItem1Sets>
    init {

    }
}