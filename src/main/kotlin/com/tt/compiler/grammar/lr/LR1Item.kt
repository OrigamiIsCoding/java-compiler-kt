package com.tt.compiler.grammar.lr

import com.tt.compiler.exception.IllegalGrammarSymbolException
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol

/**
 * @author Origami
 * @date 5/6/2023 8:35 AM
 */
data class LR1Item(
    /**
     * 产生式
     */
    override val production: Production,
    /**
     * 点的位置
     */
    override val dot: Int,
    /**
     * 搜索符
     */
    val search: Set<Symbol>,
) : LRItem {
    override fun next(): LR0Item {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "LR(1) { ${toExpression()} }"
    }

    override fun toExpression(): String {
        val searchPart = search.joinToString(
            ", ",
            prefix = "{ ",
            postfix = " }"
        ) { it.value }
        return "${super.toExpression()} ${Symbol.Comma.value} $searchPart"
    }

    companion object {
        fun parse(line: String): LR1Item {
            val index = line.indexOf(Symbol.Comma.value)
            if (index == -1) {
                throw IllegalGrammarSymbolException("LR(1) 项目中必须存在逗号(${Symbol.Comma.value})分隔产生式与搜索符")
            }
            val (production, dot) = LRItem.parseProduction(line.take(index))
            val search = parseSearch(line.takeLast(line.length - index - 1))
            return LR1Item(production, dot, search)
        }

        /**
         * 解析搜索符
         * 格式 "{ ...Symbols }"
         * @param search 搜索符号
         * @return 搜索符集合
         */
        private fun parseSearch(search: String): Set<Symbol> {
            return search.trim()
                .trim('{', '}')
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .map { Symbol.from(it) }
                .toSet()
        }
    }
}