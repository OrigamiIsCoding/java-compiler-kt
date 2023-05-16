package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.Terminal

/**
 * @author Origami
 * @date 5/16/2023 10:24 AM
 */

fun LR1Item.toLR0Item() = LR0Item(production, dot)
fun LR0Item.toLR1Item(lookAhead: Set<Terminal>) = LR1Item(production, lookAhead, dot)