package com.tt.compiler.component.impl

import com.tt.compiler.component.GrammarAnalyzer
import com.tt.compiler.exception.IllegalGrammarSymbolException
import com.tt.compiler.grammar.*
import com.tt.compiler.grammar.ll.LL1ParseTable
import java.util.*

/**
 * @author Origami
 * @date 4/18/2023 7:56 PM
 */
class GrammarAnalyzerLL1Impl(override val grammar: Grammar) : GrammarAnalyzer {

    private val parseTable: LL1ParseTable

    init {
        // 构建 FirstSet、FollowSet、LL(1) Table
        val firstSet = FirstSet.from(grammar)
        // 只有当 FirstSet 中包括空串时，才需要构建 FollowSet
        val followSet = if (firstSet.values.flatten().map { it.first }.contains(Symbol.Empty)) {
            FollowSet.from(grammar, firstSet)
        } else {
            FollowSet.Empty
        }
        parseTable = LL1ParseTable(firstSet, followSet)
    }

    override fun analyze(sentence: String): List<Production> {
        // 输入的句子符号
        val input = sentence.toInputSymbols()
        // 解析的产生式
        val parseProductions = mutableListOf<Production>()
        // 分析栈，刚开始的时候压入 End 和 Start
        val stack = Stack<Symbol>().apply {
            push(Symbol.End)
            push(Symbol.Start)
        }

        while (stack.isNotEmpty() && input.notOver) stack.apply {
            // 当前输入符号
            val current = input.current
            // 获取当前栈顶符号
            val top = pop()

            if (top == current) {
                input.next()
            } else {
                parseTable[top]?.get(current)?.let {
                    parseProductions.add(it)
                    if (it.right.first() != Symbol.Empty) {
                        // 将产生式的右部压入栈中
                        it.right.reversed().forEach(::push)
                    }
                } ?: throw IllegalGrammarSymbolException("输入的句子 $sentence 不符合该文法")
            }
        }

        if (stack.isNotEmpty() || input.notOver) {
            throw IllegalGrammarSymbolException("输入的句子 $sentence 不符合该文法")
        }

        return parseProductions
    }
}