package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.NonTerminal
import com.tt.compiler.grammar.Terminal

/**
 * @author Origami
 * @date 4/24/2023 10:15 AM
 */
interface LRParseTable {
    val action: Map<Int, Map<Terminal, Action>>
    val goto: Map<Int, Map<NonTerminal, Int>>
}