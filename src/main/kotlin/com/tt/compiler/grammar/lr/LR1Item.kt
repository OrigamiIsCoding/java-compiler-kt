package com.tt.compiler.grammar.lr

import com.tt.compiler.exception.IllegalGrammarSymbolException
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol
import com.tt.compiler.grammar.Terminal

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
     * 搜索符
     */
    val lookAhead: Set<Terminal>,
    /**
     * 点的位置
     */
    override val dot: Int = 0,
) : LRItem {

    override fun next(): LR1Item {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        return LR1Item(production, lookAhead, dot + 1)
    }

    override fun toString(): String {
        return "LR(1) { ${toExpression()} }"
    }

    override fun toExpression(): String {
        val lookAheadPart = lookAhead.joinToString(
            ", ",
            prefix = "LookAhead { ",
            postfix = " }"
        ) { it.value }
        return "${super.toExpression()} $lookAheadPart"
    }

    companion object {
        private const val LookAhead = "LookAhead"

        val Start = LR1Item(Production.ExtendedProduction, setOf(Symbol.End))

        val Accept = Start.next()

        fun parse(line: String): LR1Item {
            val index = line.indexOf(LookAhead)
            if (index == -1) {
                throw IllegalGrammarSymbolException("LR(1) 项目中必须存在($LookAhead)分隔产生式与搜索符")
            }
            val (production, dot) = LRItem.parseProduction(line.take(index))
            val lookAhead = parseLookAhead(line.takeLast(line.length - index - 1 - LookAhead.length))
            return LR1Item(production, lookAhead, dot = dot)
        }

        /**
         * 解析搜索符
         * 格式 "{ ...Symbols }"
         * @param search 搜索符号
         * @return 搜索符集合
         */
        private fun parseLookAhead(search: String): Set<Terminal> {
            return search.trim()
                .trim('{', '}')
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .map { Symbol.terminal(it) }
                .toSet()
        }
    }
}