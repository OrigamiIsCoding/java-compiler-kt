package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.*

/**
 * LR(0) 自动机
 * @author Origami
 * @date 4/23/2023 1:01 PM
 */
class LR0Automata(grammar: Grammar) {

    init {
        val extendedGrammar = grammar.toExtended()
    }

    private fun List<Production>.buildLRZeroItems() {
        val start = LR0Item(Production.ExtendedProduction)
        this.itemSetsClosure(start)
    }

    private fun List<Production>.itemSetsClosure(item: LR0Item) {
        val itemSets = mutableSetOf(item)
        val processQueue = ArrayDeque<LR0Item>().apply {
            addLast(item)
        }
        while (processQueue.isNotEmpty()) processQueue.apply {
            val item = removeFirst()
            when (item.wait) {
                is Terminal -> TODO()
                null -> TODO()
                is NonTerminal -> {

                }
            }
        }
    }
}