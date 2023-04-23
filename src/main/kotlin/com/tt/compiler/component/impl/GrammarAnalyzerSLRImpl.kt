package com.tt.compiler.component.impl

import com.tt.compiler.component.GrammarAnalyzer
import com.tt.compiler.grammar.LRZeroItem
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol

class GrammarAnalyzerSLRImpl(lines: List<String>) : GrammarAnalyzer(lines) {
    init {
        this.productions = this.productions.toExtendedGrammar()
    }

    override fun analyze(sentence: String): List<Production> {
        TODO("Not yet implemented")
    }

    private fun List<Production>.toExtendedGrammar(): List<Production> {
        // 检查是否存在拓广文法的起始符号
        if (this.any { it.left == Symbol.ExtendedStart || it.right.contains(Symbol.ExtendedStart) }) {
            throw IllegalStateException("转为扩广文法冲突，原始文法中包含 ${Symbol.ExtendedStart.value}(拓广文法的起始符号)")
        }
        return listOf(Production.ExtendedProduction) + this
    }

    private fun List<Production>.buildLRZeroItems() {
        val itemSets = mutableSetOf(LRZeroItem(Production.ExtendedProduction))
        val productionMap = this.groupBy(Production::left)
        val queue = ArrayDeque<LRZeroItem>()
        TODO()
    }
}