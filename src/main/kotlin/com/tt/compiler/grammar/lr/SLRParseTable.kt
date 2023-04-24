package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.FollowSet
import com.tt.compiler.grammar.NonTerminal
import com.tt.compiler.grammar.Symbol
import com.tt.compiler.grammar.Terminal

/**
 * SLR 分析表，与 LR(0) 不同的是，规约状态只有是当前左部的 Follow 集中的终结符才会加入到表中
 * @author Origami
 * @date 4/23/2023 12:51 PM
 */
class SLRParseTable(automaton: LR0Automaton, followSet: FollowSet) : LRParseTable {
    override val action: Map<Int, Map<Terminal, Action>>
    override val goto: Map<Int, Map<NonTerminal, Int>>
    override val startState: Int = automaton.start.value
    private val states = automaton.states.map { it.value }

    init {
        val actionTable = mutableMapOf<Int, MutableMap<Terminal, Action>>()
        val gotoTable = mutableMapOf<Int, MutableMap<NonTerminal, Int>>()

        // 遍历状态机的所有状态
        automaton.states.forEach { state ->
            val goto = gotoTable.getOrPut(state.value) { mutableMapOf() }
            val action = actionTable.getOrPut(state.value) { mutableMapOf() }

            // 遍历当前状态可以接受的符号以及到达的状态
            state.forEach { accept, nextNode ->
                when (accept) {
                    // 如果通过的是非终结符，则将状态转换加入到 goto 表中
                    is NonTerminal -> goto[accept] = nextNode.value
                    // 如果通过的是终结符，则将状态转换加入到 action 表中，同时标注该状态为移进
                    is Terminal -> action[accept] = Action.Shift(nextNode.value)
                }
            }

            // 遍历当前状态的项目集闭包，过滤掉小圆点不在最后的项目
            automaton.closures[state.value].filter { !it.hasNext() }.forEach {
                when (it) {
                    // 如果产生式是 [S' -> S ·] 也就是接受状态，则加入 $ 的状态为接受
                    LR0Item.Accept -> action[Symbol.End] = Action.Accept
                    // 否则是其他产生式，那么当前是规约状态
                    // 将当前产生式加入产生式左部的 FollowSet 中的终结符的位置上
                    else -> followSet[it.production.left]?.forEach { terminal ->
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
        val indent1 = "\t".repeat(1)
        val indent2 = "\t".repeat(2)
        val indent3 = "\t".repeat(3)
        return "$indent1$tableName {\n${
            this.states
                .filterNot { table[it].isNullOrEmpty() }
                .joinToString("\n") {
                    indent2 + "State $it:" + table[it]!!.map { (key, value) ->
                        "$key -> $value"
                    }.joinToString("\n$indent3", prefix = "\n$indent3")
                }
        }\n$indent1}"
    }
}