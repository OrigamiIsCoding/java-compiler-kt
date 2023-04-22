package com.tt.compiler.component

import com.tt.compiler.exception.IllegalGrammarSymbolException
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol

/**
 * @author Origami
 * @date 4/22/2023 5:59 PM
 */
abstract class GrammarAnalyzer(lines: List<String>) {
    protected var productions: List<Production>

    init {
        // 解析产生式
        // 文件按一行一行分隔
        productions = lines.flatMap(Production::parse)
        if (productions.none { it.left.isStart() }) {
            throw IllegalGrammarSymbolException("文法的左部必须存在起始符号 ${Symbol.Start.value}")
        }
    }

    /**
     * 语法分析
     * @param sentence 待分析的句子
     * @return 产生式
     */
    abstract fun analyze(sentence: String): List<Production>
}