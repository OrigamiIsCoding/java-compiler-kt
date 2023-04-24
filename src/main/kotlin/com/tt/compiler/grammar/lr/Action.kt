package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.Production

/**
 * LR 分析表中 action 表的几个状态
 * @author Origami
 * @date 4/23/2023 11:16 PM
 */
sealed class Action {
    /**
     * 移进状态，需要将当前状态和当前符号进行栈
     */
    data class Shift(val state: Int) : Action() {
        override fun toString(): String {
            return "Shift { $state }"
        }
    }

    /**
     * 规约状态，需要将当前的状态和符号规约为一个产生式
     */
    data class Reduce(val production: Production) : Action() {
        override fun toString(): String {
            return "Reduce { $production }"
        }
    }

    /**
     * 接受状态，表示文法成功接受
     */
    object Accept : Action() {
        override fun toString(): String {
            return "Accept"
        }
    }
}