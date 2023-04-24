package com.tt.compiler.component.impl

import com.tt.compiler.grammar.FollowSet
import com.tt.compiler.grammar.Grammar
import com.tt.compiler.grammar.lr.LR0Automaton
import com.tt.compiler.grammar.lr.LRParseTable
import com.tt.compiler.grammar.lr.SLRParseTable

class GrammarAnalyzerSLRImpl(grammar: Grammar) : GrammarAnalyzerLR {

    override val parseTable: LRParseTable
    override val grammar: Grammar

    init {
        // 转换为拓广文法
        this.grammar = grammar.toExtended()
        val followSet = FollowSet.from(grammar)
        val automaton = LR0Automaton(grammar)
        this.parseTable = SLRParseTable(automaton, followSet)
    }
}

