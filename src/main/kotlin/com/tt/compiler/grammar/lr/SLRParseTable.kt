package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.FollowSet
import com.tt.compiler.grammar.NonTerminal

/**
 * @author Origami
 * @date 4/23/2023 12:51 PM
 */

private typealias ImmutableActionTable = Map<NonTerminal, Map<Int, Action>>

class SLRParseTable(automaton: LR0Automaton, followSet: FollowSet) {
    init {

    }
}