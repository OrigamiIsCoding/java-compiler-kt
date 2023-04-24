package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.Production

/**
 * @author Origami
 * @date 4/23/2023 11:16 PM
 */
sealed class Action {
    /**
     * 移进
     */
    data class Shift(val state: Int) : Action() {
        override fun toString(): String {
            return "Shift { $state }"
        }
    }

    /**
     * 规约
     */
    data class Reduce(val production: Production) : Action() {
        override fun toString(): String {
            return "Reduce { $production }"
        }
    }

    /**
     * 接受
     */
    object Accept : Action() {
        override fun toString(): String {
            return "Accept"
        }
    }
}