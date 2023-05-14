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
            mutableMapOf<Set<Production>, MutableMap<Production, MutableSet<Terminal>>>()
        val productionsToState = mutableMapOf<Set<Production>, Node<Int, Symbol>>()
        val newClosure = mutableListOf<Set<Production>>()

        // 遍历所有的状态
        states.forEach { node ->
            // 当前状态的项目集合
            val itemSet = itemSets[node.value]
            // 当前状态的所有产生式
            val productions = itemSet.map { it.production }.toSet()

            // 如果当前产生式集合没有出现过，则新建一个状态
            productionsToState.getOrPut(productions) {
                newClosure.add(productions)
                if (node is Node.Accept) {
                    Node.Accept(newStateMap.size)
                } else {
                    Node.Reject(newStateMap.size)
                }
            }

            newLookAheadMap.getOrPut(productions) {
                // 初始化新状态的 lookAheadMap
                productions.associateWithTo(mutableMapOf()) {
                    mutableSetOf()
                }
            }.let {
                // 将当前状态的 lookAheadMap 合并到新状态的 lookAheadMap 中
                itemSet.forEach { (production, lookAhead) ->
                    it[production]!!.addAll(lookAhead)
                }
            }

            // 设置 原始状态->新状态 的映射
            // 如果产生式集合出现过，则直接使用之前的状态
            newStateMap[node] = productionsToState[productions]!!
        }

        // 重新遍历一遍所有的状态，将状态转换的映射也进行替换
        states.forEach { node ->
            val newNode = newStateMap[node]!!
            node.forEach { symbol, nextNode ->
                newNode[symbol] = newStateMap[nextNode]!!
            }
        }

        return LR1Automaton(
            start = newStateMap[this.start]!!,
            states = productionsToState.values.toList(),
            itemSets = newClosure.map { closure ->
                closure.map {
                    LR1Item(it, newLookAheadMap[closure]!![it]!!)
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

    private fun Map<NonTerminal, List<Production>>.goto(items: List<LR1Item>, firstSet: FirstSet): ClosureOfItem1Sets {
        return items.map { it.next() }
            .flatMap { closure(it, firstSet) }
            .toSet()
    }
}