package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.FollowSet
import com.tt.compiler.grammar.NonTerminal
import com.tt.compiler.grammar.Symbol
import com.tt.compiler.grammar.Terminal

/**
 * @author Origami
 * @date 4/23/2023 12:51 PM
 */


class SLRParseTable(automaton: LR0Automaton, followSet: FollowSet) {
    val action: Map<Int, Map<Terminal, Action>>
    val goto: Map<Int, Map<NonTerminal, Int>>
    val states = automaton.states.map { it.value }

    init {
        val actionTable = mutableMapOf<Int, MutableMap<Terminal, Action>>()
        val gotoTable = mutableMapOf<Int, MutableMap<NonTerminal, Int>>()

        // 添加 Accept 项目
        automaton.states.forEach { state ->
            val goto = gotoTable.getOrPut(state.value) { mutableMapOf() }
            val action = actionTable.getOrPut(state.value) { mutableMapOf() }
            state.forEach { accept, nextNode ->
                when (accept) {
                    is NonTerminal -> goto[accept] = nextNode.value
                    is Terminal -> action[accept] = Action.Shift(nextNode.value)
                }
            }

            automaton.closures[state.value].filter { !it.hasNext() }.forEach {
                if (it == LR0Item.Accept) {
                    action[Symbol.End] = Action.Accept
                } else {
                    followSet[it.production.left]?.forEach { terminal ->
                        action[terminal] = Action.Reduce(it.production)
                    }
                }
            }
        }

        action = actionTable
        goto = gotoTable
    }

    override fun toString(): String {
        return "SLRParseTable {" + this.states.map {
            action[it]!!.map { (terminal, action) ->
                "\taction[$it][$terminal] = $action"
            } + goto[it]!!.map { (nonTerminal, state) ->
                "\tgoto[$it][$nonTerminal] = $state"
            }.joinToString("\n")
        }.joinToString("\n") + "}"
    }


}