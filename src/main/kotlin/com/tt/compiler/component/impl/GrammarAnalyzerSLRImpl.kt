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
        // 求 Follow 集合
        val followSet = FollowSet.from(grammar)
        // 构建活前缀 DFA
        val automaton = LR0Automaton(grammar)
        // 构建 SLR 分析表
        this.parseTable = SLRParseTable(automaton, followSet)
    }
}

