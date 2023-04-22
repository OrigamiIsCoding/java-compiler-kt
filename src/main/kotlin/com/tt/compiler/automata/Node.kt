package com.tt.compiler.automata

/**
 * @author Origami
 * @date 4/22/2023 6:55 PM
 */
class Node<T, Accept>(
    /**
     * 节点的值
     */
    val value: T,
    /**
     * 是否是接受状态
     */
    val isAccept: Boolean = false,
    /**
     * 节点的子节点
     */
    val next: MutableMap<Accept, Node<T, Accept>> = mutableMapOf()
)