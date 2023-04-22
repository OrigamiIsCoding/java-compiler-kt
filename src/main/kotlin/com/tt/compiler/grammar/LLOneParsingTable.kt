package com.tt.compiler.grammar

/**
 * @author Origami
 * @date 4/20/2023 5:51 PM
 */
class LLOneParsingTable(firstSet: FirstSet, followSet: FollowSet) :
    HashMap<Symbol.NonTerminal, Map<Symbol.Terminal, Production>>() {
    init {
        firstSet.forEach { (left, leftFirstSet) ->
            val leftMap = mutableMapOf<Symbol.Terminal, Production>()
            leftFirstSet.forEach { (first, production) ->
                if (first.isEmpty()) {
                    // 如果当前符号是空串，像表中添加 symbol -> 空串, symbol 属于 Follow(left)
                    val productionEmpty = Production(left, listOf(Symbol.Empty))
                    followSet[left]!!.forEach { leftMap[it] = productionEmpty }
                } else {
                    // 否则添加该终结符对对应的产生式
                    leftMap[first] = production
                }
            }
            this[left] = leftMap
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