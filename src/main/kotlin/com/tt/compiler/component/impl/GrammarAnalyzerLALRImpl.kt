package com.tt.compiler.component.impl

import com.tt.compiler.grammar.FirstSet
import com.tt.compiler.grammar.Grammar
import com.tt.compiler.grammar.lr.LALRParseTable
import com.tt.compiler.grammar.lr.LR1Automaton
import com.tt.compiler.grammar.lr.LRParseTable

/**
 * @author Origami
 * @date 5/6/2023 8:56 AM
 */
class GrammarAnalyzerLALRImpl(
    grammar: Grammar
) : GrammarAnalyzerLR {
    override val grammar: Grammar
    override val parseTable: LRParseTable

    init {
        this.grammar = grammar.toExtended()
        // 构建 FirstSet、LR(1) Table
        val firstSet = FirstSet.from(this.grammar)
        val automaton = LR1Automaton(this.grammar, firstSet)
        this.parseTable = LALRParseTable(automaton)
    }
}