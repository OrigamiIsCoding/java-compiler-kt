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
    private val states = automaton.states.map { it.value }

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
        return "SLRParseTable {\n${formatTable("ActionTable", action)},\n${formatTable("GotoTable", goto)}\n}"
    }

    /**
     * 格式化表格
     * \tTable {
     * \t\tState 0:
     * \t\t\tTerminal -> Action
     * \t}
     * @param tableName 表格名
     * @param table 表格
     * @return 格式化后的表格
     */
    private fun formatTable(tableName: String, table: Map<Int, Map<*, *>>): String {
        val indent = "\t".repeat(1)
        val indent1 = "\t".repeat(2)
        val indent2 = "\t".repeat(3)
        return "$indent$tableName {\n" + this.states
            .filterNot { table[it].isNullOrEmpty() }
            .joinToString("\n") {
                indent1 + "State $it:" + table[it]!!.map { (key, value) ->
                    "$key -> $value"
                }.joinToString("\n$indent2", prefix = "\n$indent2")
            } + "\n$indent}"

    }

}