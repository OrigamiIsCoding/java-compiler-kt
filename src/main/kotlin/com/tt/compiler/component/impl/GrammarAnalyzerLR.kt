package com.tt.compiler.component.impl

import com.tt.compiler.component.GrammarAnalyzer
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol
import com.tt.compiler.grammar.lr.Action
import com.tt.compiler.grammar.lr.LRParseTable
import java.util.*

/**
 * @author Origami
 * @date 4/24/2023 10:24 AM
 */
interface GrammarAnalyzerLR : GrammarAnalyzer {
    val parseTable: LRParseTable
    override fun analyze(sentence: String): List<Production> {
        val input = sentence.toInputSymbols()
        // 解析的产生式
        val parseProductions = mutableListOf<Production>()

        // 状态栈
        val stateStack = Stack<Int>().apply {
            push(parseTable.startState)
        }
        // 符号栈
        val symbolStack = Stack<Symbol>().apply {
            push(Symbol.End)
        }
        while (true) {
            check(input.notOver && stateStack.isNotEmpty()) { "语法分析失败，该句子不属于该文法" }
            val current = input.current
            val topState = stateStack.peek()
            parseTable.action[topState]!![current]?.also { action ->
                when (action) {
                    Action.Accept -> return parseProductions
                    // 当前是规约状态，则将产生式的右部弹出符号栈，将产生式的左部压入符号栈
                    is Action.Reduce -> {
                        val production = action.production
                        parseProductions.add(production)

                        for (i in 0 until production.right.size) {
                            symbolStack.pop()
                            stateStack.pop()
                        }

                        val left = production.left

                        symbolStack.push(left)
                        // 查询 goto 表，将 goto 表中的状态压入状态栈
                        parseTable.goto[stateStack.peek()]!![left]?.let {
                            stateStack.push(it)
                        } ?: throw IllegalStateException("goto 表中没有找到状态，该句子不属于该文法")
                    }
                    // 当前是移入状态，则将当前输入符号压入符号栈，将当前状态压入状态栈
                    is Action.Shift -> {
                        symbolStack.push(current)
                        stateStack.push(action.state)
                        input.next()
                    }
                }
            } ?: throw IllegalStateException("action 表中没有找到状态，该句子不属于该文法")
        }
    }
}