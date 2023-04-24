package com.tt.compiler.automata

/**
 * @author Origami
 * @date 4/23/2023 1:50 PM
 */
interface Automaton<T, Accept> {
    val start: Node<T, Accept>
    val states: List<Node<T, Accept>>
    val accepts: List<Node.Accept<T, Accept>>
        get() {
            return states.filterIsInstance<Node.Accept<T, Accept>>()
        }
    val rejects: List<Node.Reject<T, Accept>>
        get() {
            return states.filterIsInstance<Node.Reject<T, Accept>>()
        }

    operator fun get(t: T): Node<T, Accept>? {
        return states.find { it.value == t }
    }
}