package com.tt.compiler.component.impl

import com.tt.compiler.component.GrammarAnalyzer
import com.tt.compiler.grammar.Grammar
import com.tt.compiler.grammar.Production

class GrammarAnalyzerSLRImpl : GrammarAnalyzer {
    constructor(lines: List<String>) : super(lines)
    constructor(inputGrammar: String) : super(inputGrammar)

    // 转换为拓广文法
    constructor(grammar: Grammar) : super(grammar.toExtended())

    override fun analyze(sentence: String): List<Production> {
        TODO("Not yet implemented")
    }
}

