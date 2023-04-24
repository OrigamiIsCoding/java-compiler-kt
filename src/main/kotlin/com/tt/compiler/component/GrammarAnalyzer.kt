package com.tt.compiler.component

import com.tt.compiler.grammar.Grammar
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol

/**
 * @author Origami
 * @date 4/22/2023 5:59 PM
 */
interface GrammarAnalyzer {
    val grammar: Grammar

    /**
     * 语法分析
     * @param sentence 待分析的句子
     * @return 产生式
     */
    fun analyze(sentence: String): List<Production>

    fun String.toInputSymbols(): InputBuffer {
        val inputSymbols = this.split(" ")
            .filter(String::isNotBlank)
            .map(Symbol::from)
            .toMutableList()

        if (inputSymbols.isEmpty()) {
            return InputBuffer(emptyList())
        } else if (inputSymbols.last() != Symbol.End) {
            // 如果句子的最后一个符号不是结束符号，则添加结束符号
            inputSymbols.add(Symbol.End)
        }
        return InputBuffer(inputSymbols)
    }

    class InputBuffer(
        private val symbols: List<Symbol>,
        private var index: Int = 0
    ) {
        val current get() = symbols[index]
        val notOver get() = index < symbols.size
        fun next() {
            index++
        }
    }
}