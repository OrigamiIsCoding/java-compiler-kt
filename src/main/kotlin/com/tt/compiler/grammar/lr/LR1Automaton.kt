package com.tt.compiler.grammar.lr

import com.tt.compiler.automata.Automaton
import com.tt.compiler.automata.Node
import com.tt.compiler.grammar.*

/**
 * @author Origami
 * @date 5/6/2023 9:34 AM
 */

private typealias ClosureOfItem1Sets = Set<LR1Item>

class LR1Automaton : Automaton<Int, Symbol> {
    override val start: Node<Int, Symbol>
    override val states: List<Node<Int, Symbol>>
    val itemSets: List<ClosureOfItem1Sets>

    /**
     * 根据扩展文法和 FirstSet 构造 LR(1) 自动机
     */
    constructor(extendedGrammar: Grammar, firstSet: FirstSet) {
        val productionMap = extendedGrammar.groupBy { it.left }
        val startClosure = productionMap.closure(LR1Item.Start, firstSet)
        start = Node.Reject(0)
        val allStates = mutableMapOf<ClosureOfItem1Sets, Node<Int, Symbol>>(
            startClosure to start
        )
        val processQueue = mutableListOf(startClosure)
        var index = 0
        while (index < processQueue.size) {
            // 取出状态
            val state = processQueue[index++]

            // 遍历这个状态，需要可以移动的项目
            state.filter { it.hasNext() }
                .groupBy { it.wait } // 根据当前等待的符号进行分组
                .forEach { (accept, nextItems) ->
                    val nextState = productionMap.goto(nextItems, firstSet)

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
        itemSets = processQueue
    }

    /**
     * 合并同心集的内部构造器
     */
    private constructor(
        start: Node<Int, Symbol>,
        states: List<Node<Int, Symbol>>,
        itemSets: List<ClosureOfItem1Sets>
    ) {
        this.start = start
        this.states = states
        this.itemSets = itemSets
    }

    /**
     * 合并同心集
     * Note: 合并同心集不会产生移进规约冲突，但是会产生规约规约冲突，且可能会推迟错误的发现
     * @return 合并同心集后的 LR(1) 自动机
     */
    fun mergeIdenticalKernelItemSets(): LR1Automaton {
        // 新的所有状态
        val newStateMap = mutableMapOf<Node<Int, Symbol>, Node<Int, Symbol>>()
        // 新的 LR(1) 项目集的 lookAhead
        val newLookAheadMap =
            mutableMapOf<Set<LR0Item>, MutableMap<LR0Item, MutableSet<Terminal>>>()
        val lr0ItemsToNewState = mutableMapOf<Set<LR0Item>, Node<Int, Symbol>>()
        val newClosure = mutableListOf<Set<LR0Item>>()

        // 遍历所有的状态
        states.forEach { node ->
            // 当前状态的项目集合
            val itemSet = itemSets[node.value]
            // 当前状态的所有产生式
            val lr0Items = itemSet.map { it.toLR0Item() }.toSet()

            // 如果当前产生式集合没有出现过，则新建一个状态
            lr0ItemsToNewState.getOrPut(lr0Items) {
                newClosure.add(lr0Items)
                if (node is Node.Accept) {
                    Node.Accept(lr0ItemsToNewState.size)
                } else {
                    Node.Reject(lr0ItemsToNewState.size)
                }
            }

            newLookAheadMap.getOrPut(lr0Items) {
                // 初始化新状态的 lookAheadMap
                lr0Items.associateWithTo(mutableMapOf()) {
                    mutableSetOf()
                }
            }.let {
                // 将当前状态的 lookAheadMap 合并到新状态的 lookAheadMap 中
                itemSet.forEach { lr1Item ->
                    it[lr1Item.toLR0Item()]!!.addAll(lr1Item.lookAhead)
                }
            }

            // 设置 原始状态->新状态 的映射
            // 如果产生式集合出现过，则直接使用之前的状态
            newStateMap[node] = lr0ItemsToNewState[lr0Items]!!
        }

        // 重新遍历一遍所有的状态，将状态转换的映射也进行替换
        states.forEach { oldNode ->
            newStateMap[oldNode]!!.let {
                oldNode.forEach { symbol, oldNextNode ->
                    it[symbol] = newStateMap[oldNextNode]!!
                }
            }
        }

        return LR1Automaton(
            start = newStateMap[this.start]!!,
            states = lr0ItemsToNewState.values.toList(),
            itemSets = newClosure.map { closure ->
                closure.map {
                    it.toLR1Item(newLookAheadMap[closure]!![it]!!)
                }.toSet()
            }
        )
    }

    private fun Map<NonTerminal, List<Production>>.closure(lr1Item: LR1Item, firstSet: FirstSet): ClosureOfItem1Sets {
        // 当当前项目走到了结尾 或者 当前项目等待的是终结符，则直接返回
        if (!lr1Item.hasNext() || lr1Item.wait is Terminal) {
            return setOf(lr1Item)
        }

        // 项目集
        val itemSets = mutableSetOf<LR1Item>()
        val processQueue = ArrayDeque<LR1Item>().apply {
            addLast(lr1Item)
        }

        while (processQueue.isNotEmpty()) {
            val item = processQueue.removeFirst()
            itemSets.add(item)

            // 如果当前项目的等待符号是非终结符
            if (item.wait is NonTerminal) {
                // 遍历所有的产生式
                this[item.wait]?.forEach { production ->
                    // 当前可以移动到的等价产生式

                    // 判断当前的 lookAhead
                    val lookAhead = item.waitK(1).let { symbol ->
                        when (symbol) {
                            is Symbol.NonTerminal -> firstSet[symbol]!!.map { it.first }.toSet()
                            else -> item.lookAhead
                        }
                    }

                    LR1Item(production, lookAhead)
                        .takeIf { it !in itemSets }
                        ?.let {
                            println("add $it")
                            processQueue.addLast(it)
                        }
                }
            }
        }
        println("end ..")
        return itemSets
    }

    private fun Map<NonTerminal, List<Production>>.goto(items: List<LR1Item>, firstSet: FirstSet): ClosureOfItem1Sets {
        return items.map { it.next() }
            .flatMap { closure(it, firstSet) }
            .toSet()
    }
}