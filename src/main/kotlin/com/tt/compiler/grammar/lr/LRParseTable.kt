package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.NonTerminal
import com.tt.compiler.grammar.Terminal

/**
 * LR 分析表
 * @author Origami
 * @date 4/24/2023 10:15 AM
 */
interface LRParseTable {
    /**
     * Action 表，用于判断当前状态所需要执行的动作
     * @see Action
     */
    val action: Map<Int, Map<Terminal, Action>>

    /**
     * Goto 表，判断与当前状态等价的状态
     */
    val goto: Map<Int, Map<NonTerminal, Int>>

    /**
     * 开始状态
     */
    val startState: Int
}