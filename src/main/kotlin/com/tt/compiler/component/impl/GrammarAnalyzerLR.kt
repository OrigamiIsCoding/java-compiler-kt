package com.tt.compiler.component.impl

import com.tt.compiler.component.GrammarAnalyzer
import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol
import com.tt.compiler.grammar.lr.Action
import com.tt.compiler.grammar.lr.LRParseTable
import java.util.*

/**
 * LR 分析器，对于 LR 分析表的结构和含义都是一样的，所以可以抽象一层
 * 而具体构建的过程 LR(0), SLR 和 LALR 是不一样的，所以交由子类实现构建表
 * @author Origami
 * @date 4/24/2023 10:24 AM
 */
interface GrammarAnalyzerLR : GrammarAnalyzer {
    /**
     * LR 分析表
     */
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
            // 合法性校验
            check(input.notOver && stateStack.isNotEmpty()) { "语法分析失败，该句子不属于该文法" }

            // 当前输入符号
            val current = input.current

            // 查询当前栈顶状态的 action 表
            parseTable.action[stateStack.peek()]!![current]?.also { action ->
                when (action) {
                    // 接受状态，返回解析的产生式
                    Action.Accept -> return parseProductions
                    // 规约状态
                    is Action.Reduce -> {
                        val production = action.production
                        parseProductions.add(production)

                        // 将栈弹出产生式右部符号个数
                        for (i in 0 until production.right.size) {
                            symbolStack.pop()
                            stateStack.pop()
                        }

                        val left = production.left

                        // 产生式的左部入栈
                        symbolStack.push(left)

                        // 查询 goto 表，将 goto[left] 表中的状态压入状态栈
                        parseTable.goto[stateStack.peek()]!![left]?.also {
                            stateStack.push(it)
                        } ?: throw IllegalStateException("goto 表中没有找到状态，该句子不属于该文法")
                    }
                    // 移入状态
                    is Action.Shift -> {
                        // 将当前符号和状态入栈
                        symbolStack.push(current)
                        stateStack.push(action.state)
                        // 输入指针右移
                        input.next()
                    }
                }
            } ?: throw IllegalStateException("action 表中没有找到状态，该句子不属于该文法")
        }
    }
}