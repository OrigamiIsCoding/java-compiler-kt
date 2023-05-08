package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.NonTerminal
import com.tt.compiler.grammar.Terminal

/**
 * LR 分析表
 * @author Origami
 * @date 4/24/2023 10:15 AM
 */
interface LRParseTable {
    /**
     * Action 表，用于判断当前状态所需要执行的动作
     * @see Action
     */
    val action: Map<Int, Map<Terminal, Action>>

    /**
     * Goto 表，判断与当前状态等价的状态
     */
    val goto: Map<Int, Map<NonTerminal, Int>>

    /**
     * 开始状态
     */
    val startState: Int


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
    fun formatTable(tableName: String, table: Map<Int, Map<*, *>>): String {
        val indent1 = "\t".repeat(1)
        val indent2 = "\t".repeat(2)
        val indent3 = "\t".repeat(3)
        return "$indent1$tableName {\n${
            table.keys.sorted()
                .joinToString("\n") {
                    indent2 + "State $it:" + table[it]!!.map { (key, value) ->
                        "$key -> $value"
                    }.joinToString("\n$indent3", prefix = "\n$indent3")
                }
        }\n$indent1}"
    }
}