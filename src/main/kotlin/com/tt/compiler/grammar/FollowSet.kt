package com.tt.compiler.grammar

/**
 * @author Origami
 * @date 4/20/2023 12:11 PM
 */
private typealias ImmutableFollowSet = Map<NonTerminal, Set<Terminal>>
private typealias MutableFollowSet = MutableMap<NonTerminal, MutableSet<Terminal>>

class FollowSet private constructor(firstSet: FirstSet, grammar: Grammar) :
    ImmutableFollowSet by buildFollowSet(firstSet, grammar) {
    companion object {
        val Empty = FollowSet(FirstSet.Empty, Grammar.Empty)

        fun from(grammar: Grammar): FollowSet {
            return from(grammar, FirstSet.from(grammar))
        }

        fun from(grammar: Grammar, firstSet: FirstSet): FollowSet {
            return FollowSet(firstSet, grammar)
        }

        /**
         * 构建 FollowSet
         * @param firstSet FirstSet
         * @param grammar 产生式
         * @return FollowSet
         */
        private fun buildFollowSet(firstSet: FirstSet, grammar: Grammar): ImmutableFollowSet {
            if (grammar.isEmpty()) {
                return emptyMap()
            }
            val map = mutableMapOf<NonTerminal, MutableSet<Terminal>>()
            map[if (grammar.isExtended) Symbol.ExtendedStart else Symbol.Start] = mutableSetOf(Symbol.End)
            while (true) {
                if (!map.update(firstSet, grammar)) {
                    break
                }
            }
            return map.mapValues { it.value.toSet() }
        }

        /**
         * 遍历一遍产生式更新现有的 FollowSet
         * @param firstSet FirstSet
         * @param grammar 产生式
         * @return 如果更新成功返回 true，否则返回 false
         */
        private fun MutableFollowSet.update(firstSet: FirstSet, grammar: Grammar): Boolean {
            var updated = false
            grammar.forEach { (left, rightSymbols) ->
                // 倒序遍历产生式的右部
                for (last in rightSymbols.reversed()) {
                    when (last) {
                        // 如果当前符号是终结符或者和左部相等则退出
                        is Terminal -> break
                        left -> break
                        is NonTerminal -> {
                            // 将 Follow(left) 加入到 Follow(last)
                            this[left]?.let {
                                updated = updated || this.getOrPut(last) { mutableSetOf() }.addAll(it)
                            }
                            // 如果 First(last) 包含空，则继续循环
                            if (firstSet[last]?.any { it.first.isEmpty() } != true) {
                                break
                            }
                        }
                    }
                }

                // 遍历产生式的右部，每次取出相邻两项
                // 左边这一项不能是终结符
                for ((current, next) in rightSymbols.zip(rightSymbols.drop(1))
                    .filter { it.first is NonTerminal }) {

                    val currentFollow = this.getOrPut(current as NonTerminal) { mutableSetOf() }
                    when (next) {
                        // 下一项是终结符，则加入到 Follow(current) 中
                        is Terminal -> {
                            updated = updated || currentFollow.add(next)
                        }
                        // 下一项是非终结符，将 {First(next) - 空串} 加入到 Follow(current)
                        is NonTerminal -> {
                            firstSet[next]?.map { it.first }?.let {
                                updated = updated || currentFollow.addAll(it.filterNot(Symbol::isEmpty))
                            }
                        }
                    }
                }
            }
            return updated
        }
    }

    override fun toString(): String {
        return "FollowSet {\n" + this.map {
            "\t${it.key} => ${it.value}"
        }.joinToString("\n") + "\n}"
    }
}

