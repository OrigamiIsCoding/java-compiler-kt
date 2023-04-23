package com.tt.compiler.component

import com.tt.compiler.grammar.Grammar
import com.tt.compiler.grammar.Production

/**
 * @author Origami
 * @date 4/22/2023 5:59 PM
 */
abstract class GrammarAnalyzer {

    constructor(grammar: Grammar) {
        this.grammar = grammar
    }

    constructor(inputGrammar: String) : this(inputGrammar.split("\n"))
    constructor(lines: List<String>) : this(Grammar(lines.flatMap(Production::parse)))

    protected var grammar: Grammar

    /**
     * 语法分析
     * @param sentence 待分析的句子
     * @return 产生式
     */
    abstract fun analyze(sentence: String): List<Production>
}