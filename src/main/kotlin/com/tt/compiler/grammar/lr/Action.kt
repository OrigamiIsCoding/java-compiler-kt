package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.Production

/**
 * @author Origami
 * @date 4/23/2023 11:16 PM
 */
sealed class Action {
    class Shift(val state: Int) : Action()
    class Reduce(val production: Production) : Action()
}