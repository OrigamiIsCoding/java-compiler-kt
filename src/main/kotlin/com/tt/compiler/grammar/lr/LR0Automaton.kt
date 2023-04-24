package com.tt.compiler.grammar.lr

import com.tt.compiler.automata.Automaton
import com.tt.compiler.automata.Node
import com.tt.compiler.grammar.*

/**
 * LR(0) 自动机
 * @author Origami
 * @date 4/23/2023 1:01 PM
 */
private typealias ClosureOfItemSets = Set<LR0Item>

class LR0Automaton(
    grammar: Grammar,
) : Automaton<Int, Symbol> {

    override val start: Node<Int, Symbol>
    override val states: List<Node<Int, Symbol>>
    val closures: List<ClosureOfItemSets>

    init {
        // 先将文法转换为扩展文法
        val extendedGrammar = grammar.toExtended()
        // 用于求等价项目的 Map
        val productionMap = extendedGrammar.groupBy { it.left }

        // 先求开始文法 S' -> S 项目集闭包
        val startClosure = productionMap.closure(LR0Item.Start)

        // 开始状态
        start = Node.Reject(0)
        // 记录所有的状态，防止重复出现
        val allStates: MutableMap<ClosureOfItemSets, Node<Int, Symbol>> = mutableMapOf(
            startClosure to start
        )

        // 处理队列，对于加入没有处理完成的状态
        val processQueue = mutableListOf(startClosure)
        var index = 0
        while (index < processQueue.size) {
            // 取出状态
            val state = processQueue[index++]

            // 遍历这个状态，需要可以移动的项目
            state.filter { it.hasNext() }
                .groupBy { it.wait } // 根据当前等待的符号进行分组
                .forEach { (accept, nextItems) ->
                    val nextState = productionMap.goto(nextItems)

                    // 判断是否出现在之前状态中，没有就加入
                    if (nextState !in allStates) {
                        allStates[nextState] = if (nextState.contains(LR0Item.Accept)) {
                            Node.Accept(processQueue.size)
                        } else {
                            Node.Reject(processQueue.size)
                        }
                        processQueue.add(nextState)
                    }
                    // 加入到表中
                    allStates[state]!![accept!!] = allStates[nextState]!!
                }
        }
        states = allStates.values.sortedBy { it.value }
        closures = processQueue
    }

    /**
     * 求 LR(0) 项目集闭包
     * @param lr0Item 项目
     * @return 项目集闭包
     */

    private fun Map<NonTerminal, List<Production>>.closure(lr0Item: LR0Item): ClosureOfItemSets {
        // 当当前项目走到了结尾 或者 当前项目等待的是终结符，则直接返回
        if (!lr0Item.hasNext() || lr0Item.wait is Terminal) {
            return setOf(lr0Item)
        }

        // 项目集
        val itemSets = mutableSetOf(lr0Item)

        val processQueue = ArrayDeque<LR0Item>().apply {
            addLast(lr0Item)
        }

        while (processQueue.isNotEmpty()) {
            val item = processQueue.removeFirst()

            // 如果当前项目的等待符号是非终结符
            if (item.wait is NonTerminal) {
                // 遍历所有的产生式
                this[item.wait]?.forEach {
                    // 加入为等价项目
                    val equalItem = LR0Item(it)
                    if (equalItem !in itemSets) {
                        processQueue.addLast(equalItem)
                        itemSets.add(equalItem)
                    }
                }
            }
        }
        return itemSets
    }

    /**
     * 返回 goto(I, X) 的项目集闭包
     *
     * @see LR0Item 这里的文法符号 X 隐藏在了 LR0Item 中
     * @param items 项目集
     * @return goto(I, X) 的项目集闭包
     */
    private fun Map<NonTerminal, List<Production>>.goto(items: List<LR0Item>): ClosureOfItemSets {
        return items.map { it.next() }
            .flatMap { closure(it) }
            .toSet()
    }

    override fun toString(): String {
        return "LR(0)Automaton { start=${start.value} }"
    }
}