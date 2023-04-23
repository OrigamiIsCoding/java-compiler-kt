package com.tt.compiler.component.impl

import com.tt.compiler.component.GrammarAnalyzer
import com.tt.compiler.grammar.FirstSet
import com.tt.compiler.grammar.FollowSet
import com.tt.compiler.grammar.Grammar
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.lr.LR0Automaton
import com.tt.compiler.grammar.lr.SLRParseTable

class GrammarAnalyzerSLRImpl : GrammarAnalyzer {
    constructor(inputGrammar: String) : super(inputGrammar)

    // 转换为拓广文法
    constructor(grammar: Grammar) : super(grammar.toExtended())

    init {
        val firstSet = FirstSet.from(grammar)
        val followSet = FollowSet.from(firstSet, grammar)
        val automaton = LR0Automaton(grammar)
        parseTable = SLRParseTable(automaton, followSet)
    }

    private val parseTable: SLRParseTable

    override fun analyze(sentence: String): List<Production> {
        TODO("Not yet implemented")
    }
}

