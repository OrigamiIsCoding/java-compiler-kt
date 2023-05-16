package com.tt.compiler.grammar

/**
 * 通过代理实现不可变的 FirstSet
 *
 * @author Origami
 * @date 4/19/2023 8:58 AM
 */

private typealias ImmutableFirstSetMap = Map<NonTerminal, Set<Pair<Terminal, Production>>>
private typealias MutableFirstSetMap = MutableMap<NonTerminal, MutableSet<Pair<Terminal, Production>>>

class FirstSet private constructor(grammar: Grammar) :
    ImmutableFirstSetMap by buildFirstSet(grammar) {
    companion object {
        val Empty = FirstSet(Grammar.Empty)

        fun from(grammar: Grammar): FirstSet {
            return if (grammar.isEmpty()) {
                Empty
            } else {
                FirstSet(grammar)
            }
        }

        /**
         * 构建 FirstSet
         * @param grammar 产生式
         * @return FirstSet
         */
        private fun buildFirstSet(grammar: Grammar): ImmutableFirstSetMap {
            val map = mutableMapOf<NonTerminal, MutableSet<Pair<Terminal, Production>>>()
            // 第一次先将 right 的第一个为终结符的加入到 First(left) 中
            grammar.forEach { production ->
                production.right.filterIsInstance(Terminal::class.java).firstOrNull()?.let {
                    map.getOrPut(production.left) { mutableSetOf() }
                        .add(it to production)
                }
            }

            while (true) {
                // 如果更新成功则需要再次更新
                if (!map.update(grammar)) {
                    break
                }
            }
            return map
        }

        /**
         * 遍历一遍产生式更新现有的 FirstSet
         * @param grammar 产生式
         * @return 如果更新成功返回 true，否则返回 false
         */
        private fun MutableFirstSetMap.update(grammar: Grammar): Boolean {
            var updated = false
            grammar.forEach { production ->
                this.getOrPut(production.left) { mutableSetOf() }.let { firstOfLeft ->
                    // 遍历产生式右边的所有非终结符
                    production.right.takeWhile { it is Symbol.NonTerminal }.all { rightSymbol ->
                        this[rightSymbol]?.let { firstOfRight ->

                            // 将 {First(rightSymbol) - 空串} 加入到 First(left)
                            updated = updated || firstOfLeft.addAll(
                                firstOfRight.map { it.first }
                                    .minus(Symbol.Empty)
                                    .map { it to production }
                            )

                            // 判断当前的 First 集合是否包括空串
                            firstOfRight.any { it.first.isEmpty() }
                        } ?: false
                    }.let {
                        if (it) {
                            updated = updated || firstOfLeft.add(Symbol.Empty to production)
                        }
                    }
                }
            }
            return updated
        }
    }

    override fun toString(): String {
        return "FirstSet {\n" + this.map { it ->
            "\t${it.key} => ${it.value.map { it.first }}"
        }.joinToString("\n") + "\n}"
    }
}

