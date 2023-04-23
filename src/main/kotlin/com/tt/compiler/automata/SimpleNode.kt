package com.tt.compiler.automata

/**
 * @author Origami
 * @date 4/23/2023 1:50 PM
 */
data class SimpleNode<T, Accept>(
    /**
     * 节点的值
     */
    val value: T,
    /**
     * 节点的子节点
     */
    private val next: MutableMap<Accept, SimpleNode<T, Accept>> = mutableMapOf()
) : MutableMap<Accept, SimpleNode<T, Accept>> by next {

    fun hasOut(): Boolean {
        return next.isNotEmpty()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleNode<*, *>) return false

        if (value != other.value) return false
        return next == other.next
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}
