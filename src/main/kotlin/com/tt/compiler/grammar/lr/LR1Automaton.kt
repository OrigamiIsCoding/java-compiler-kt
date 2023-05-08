package com.tt.compiler.grammar.lr

import com.tt.compiler.automata.Automaton
import com.tt.compiler.automata.Node
import com.tt.compiler.grammar.*

/**
 * @author Origami
 * @date 5/6/2023 9:34 AM
 */

private typealias ClosureOfItem1Sets = Set<LR1Item>

class LR1Automaton(
    extendedGrammar: Grammar,
    private val firstSet: FirstSet
) : Automaton<Int, Symbol> {
    override val start: Node<Int, Symbol>
    override val states: List<Node<Int, Symbol>>
    val closures: List<ClosureOfItem1Sets>

    init {
        // 用于求等价项目的 Map
        val productionMap = extendedGrammar.groupBy { it.left }

        // 从 S' -> · S, { $ } 开始
        val startClosure = productionMap.closure(LR1Item.Start)

        // 记录所有的状态，防止重复出现
        start = Node.Reject(0)
        val allStates: MutableMap<ClosureOfItem1Sets, Node<Int, Symbol>> = mutableMapOf(
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
                        allStates[nextState] = if (nextState.contains(LR1Item.Accept)) {
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

    private fun Map<NonTerminal, List<Production>>.closure(lr1Item: LR1Item): ClosureOfItem1Sets {
        // 当当前项目走到了结尾 或者 当前项目等待的是终结符，则直接返回
        if (!lr1Item.hasNext() || lr1Item.wait is Terminal) {
            return setOf(lr1Item)
        }

        // 项目集
        val itemSets = mutableSetOf(lr1Item)
        val processQueue = ArrayDeque<Pair<LR1Item, Set<Terminal>>>().apply {
            addLast(
                Pair(
                    lr1Item,
                    lr1Item.waitK(1).let { wait ->
                        when (wait) {
                            is Symbol.NonTerminal -> firstSet[wait]!!.map { it.first }.toSet()
                            else -> lr1Item.lookAhead
                        }
                    }
                )
            )
        }

        while (processQueue.isNotEmpty()) {
            val (item, lookAhead) = processQueue.removeFirst()

            // 如果当前项目的等待符号是非终结符
            if (item.wait is NonTerminal) {
                // 遍历所有的产生式
                this[item.wait]?.forEach { production ->
                    val equalItem = LR1Item(production, lookAhead)
                    if (equalItem !in itemSets) {
                        processQueue.addLast(Pair(equalItem, equalItem.waitK(1).let { wait ->
                            when (wait) {
                                is Symbol.NonTerminal -> firstSet[wait]!!.map { it.first }.toSet()
                                else -> equalItem.lookAhead
                            }
                        }))
                        itemSets.add(equalItem)
                    }
                }
            }
        }
        return itemSets
    }

    private fun Map<NonTerminal, List<Production>>.goto(items: List<LR1Item>): ClosureOfItem1Sets {
        return items.map { it.next() }
            .flatMap { closure(it) }
            .toSet()
    }
}