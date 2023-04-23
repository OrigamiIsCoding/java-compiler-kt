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
class GrammarAnalyzerLL1Impl : GrammarAnalyzer {
    constructor(inputGrammar: String) : super(inputGrammar)
    constructor(grammar: Grammar) : super(grammar)

    init {
        // 构建 FirstSet、FollowSet、LL(1) Table
        val firstSet = FirstSet.from(grammar)
        val followSet = FollowSet.from(firstSet, grammar)
        parseTable = LL1ParseTable(firstSet, followSet)
    }

    private val parseTable: LL1ParseTable

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

        while (stack.isNotEmpty() && inputIndex < inputSymbols.size) stack.apply {
            // 当前输入符号
            val current = inputSymbols[inputIndex]
            // 获取当前栈顶符号
            val top = pop()

            if (top == current) {
                inputIndex++
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

        if (stack.isNotEmpty() || inputIndex < inputSymbols.size) {
            throw IllegalGrammarSymbolException("输入的句子 $sentence 不符合该文法")
        }

        return parseProductions
    }
}