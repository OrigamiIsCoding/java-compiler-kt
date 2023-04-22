package com.tt.compiler.component.impl

import com.tt.compiler.component.GrammarAnalyzer
import com.tt.compiler.exception.IllegalGrammarSymbolException
import com.tt.compiler.grammar.*
import java.util.*

/**
 * @author Origami
 * @date 4/18/2023 7:56 PM
 */
class GrammarAnalyzerLLOneImpl(lines: List<String>) : GrammarAnalyzer(lines) {

    // 构建 FirstSet、FollowSet、LL(1) Table
    private val firstSet: FirstSet = FirstSet(productions)


    // 只有当 FirstSet 中包括空串时，才需要构建 FollowSet
    private val followSet: FollowSet = if (firstSet.values.flatten().map { it.first }.contains(Symbol.Empty)) {
        FollowSet(firstSet, productions)
    } else {
        FollowSet.Empty
    }

    private val parsingTable: LLOneParsingTable = LLOneParsingTable(firstSet, followSet)

    override fun analyze(sentence: String): List<Production> {
        // 输入的句子符号
        val inputSymbols = sentence.split(" ")
            .filter(String::isNotBlank)
            .map(Symbol::from)
            .toMutableList()

        if (inputSymbols.isEmpty()) {
            return emptyList()
        } else if (inputSymbols.last() != Symbol.End) {
            // 如果句子的最后一个符号不是结束符号，则添加结束符号
            inputSymbols.add(Symbol.End)
        }

        // 解析的产生式
        val parseProductions = mutableListOf<Production>()
        // 输入符号指针
        var inputIndex = 0
        // 分析栈，刚开始的时候压入 End 和 Start
        val stack = Stack<Symbol>().apply {
            push(Symbol.End)
            push(Symbol.Start)
        }

        while (stack.isNotEmpty() && inputIndex < inputSymbols.size) {
            // 当前输入符号
            val current = inputSymbols[inputIndex]
            // 获取当前栈顶符号
            val top = stack.pop()

            if (top == current) {
                inputIndex++
            } else {
                parsingTable[top]?.get(current)?.let {
                    parseProductions.add(it)
                    if (it.right.first() != Symbol.Empty) {
                        // 将产生式的右部压入栈中
                        it.right.reversed().forEach(stack::push)
                    }
                } ?: throw IllegalGrammarSymbolException("输入的句子 $sentence 不符合文法")
            }
        }

        if (stack.isNotEmpty() || inputIndex < inputSymbols.size) {
            throw IllegalGrammarSymbolException("输入的句子 $sentence 不符合文法")
        }

        return parseProductions
    }
}