package com.tt.compiler.automata

/**
 * @author Origami
 * @date 4/23/2023 1:50 PM
 */
interface SimpleAutomaton<T, Accept> {
    val start: SimpleNode<T, Accept>
    val states: Set<SimpleNode<T, Accept>>

    operator fun get(t: T): SimpleNode<T, Accept>? {
        return states.find { it.value == t }
    }
}