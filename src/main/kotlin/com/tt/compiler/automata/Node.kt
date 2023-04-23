package com.tt.compiler.automata

/**
 * @author Origami
 * @date 4/22/2023 6:55 PM
 */
sealed class Node<T, Accept>(
    /**
     * 节点的值
     */
    val value: T,
    /**
     * 节点的子节点
     */
    private val next: MutableMap<Accept, Node<T, Accept>>
) {
    class Accept<T, Accept>(
        value: T,
        next: MutableMap<Accept, Node<T, Accept>> = mutableMapOf()
    ) : Node<T, Accept>(value, next)

    class Reject<T, Accept>(
        value: T,
        next: MutableMap<Accept, Node<T, Accept>> = mutableMapOf()
    ) : Node<T, Accept>(value, next)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node<*, *>) return false

        return value == other.value
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}
