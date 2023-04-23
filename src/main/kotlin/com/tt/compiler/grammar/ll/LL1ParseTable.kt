package com.tt.compiler.grammar.ll

import com.tt.compiler.exception.IllegalGrammarSymbolException
import com.tt.compiler.grammar.*

/**
 * @author Origami
 * @date 4/20/2023 5:51 PM
 */

private typealias ImmutableLLOneParsingTable = Map<NonTerminal, Map<Terminal, Production>>


class LL1ParseTable(firstSet: FirstSet, followSet: FollowSet) :
    ImmutableLLOneParsingTable by buildLLOneParsingTable(firstSet, followSet) {
    companion object {
        /**
         * 从 FirstSet 和 FollowSet 中构建 LL(1) 分析表
         * @param firstSet FirstSet
         * @param followSet FollowSet
         * @return LL(1) Table
         */
        private fun buildLLOneParsingTable(firstSet: FirstSet, followSet: FollowSet): ImmutableLLOneParsingTable {

            val table = mutableMapOf<NonTerminal, Map<Terminal, Production>>()
            firstSet.forEach { (left, leftFirstSet) ->
                val leftMap = mutableMapOf<Terminal, Production>()
                leftFirstSet.forEach { (first, production) ->
                    if (first.isEmpty()) {
                        // 如果当前符号是空串，像表中添加 symbol -> 空串, symbol 属于 Follow(left)
                        val productionEmpty = Production(left, listOf(Symbol.Empty))
                        followSet[left]!!.forEach {
                            leftMap.checkPut(it, productionEmpty)
                        }
                    } else {
                        // 否则添加该终结符对对应的产生式
                        leftMap.checkPut(first, production)
                    }
                }
                table[left] = leftMap
            }
            return table
        }


        private fun MutableMap<Terminal, Production>.checkPut(key: Terminal, value: Production) {
            this.put(key, value)?.let {
                throw IllegalGrammarSymbolException(
                    "LL(1) 分析表构建冲突，该文法不是 LL(1) 文法"
                )
            }
        }
    }

    /**
     * |        | 终结符... |
     * |非终结符 | 产生式    |
     * | ...   |          |
     */
    override fun toString(): String {
        val terminalSymbols = this.values.flatMap { it.keys }.distinctBy { it.value }
        val nonTerminalSymbols = this.keys
        val maxWidth = maxOf(
            terminalSymbols.maxOf { it.value.length },
            nonTerminalSymbols.maxOf { it.value.length },
            this.values.flatMap { it.values }.maxOf { it.toExpression().length }
        )
        val header =
            (listOf(" ") + terminalSymbols.map(Symbol::value)).joinToString(" | ", prefix = "| ", postfix = " |") {
                String.format(
                    "%-${maxWidth}s",
                    it
                )
            }
        val rows = nonTerminalSymbols.map { nonTerminalSymbol ->
            (listOf(nonTerminalSymbol.value) + terminalSymbols.map {
                this[nonTerminalSymbol]!![it]?.toExpression() ?: "null"
            }).joinToString(" | ", prefix = "| ", postfix = " |") {
                String.format(
                    "%-${maxWidth}s",
                    it
                )
            }
        }
        return (listOf(header) + rows).joinToString("\n")
    }
}


