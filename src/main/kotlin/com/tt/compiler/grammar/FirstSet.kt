package com.tt.compiler.grammar

/**
 * 通过代理实现不可变的 FirstSet
 *
 * @author Origami
 * @date 4/19/2023 8:58 AM
 */

private typealias ImmutableFirstSetMap = Map<NonTerminal, Set<Pair<Terminal, Production>>>
private typealias MutableFirstSetMap = MutableMap<NonTerminal, MutableSet<Pair<Terminal, Production>>>

class FirstSet(grammar: Grammar) :
    ImmutableFirstSetMap by buildFirstSet(grammar) {
    companion object {
        val Empty = FirstSet(Grammar.Empty)

        /**
         * 构建 FirstSet
         * @param grammar 产生式
         * @return FirstSet
         */
        private fun buildFirstSet(grammar: Grammar): ImmutableFirstSetMap {
            val map = mutableMapOf<NonTerminal, MutableSet<Pair<Terminal, Production>>>()
            // 第一次先将 right 的第一个为终结符的加入到 First(left) 中
            grammar.forEach { production ->
                production.right.take(1)
                    .firstOrNull { it is Terminal }?.let {
                        map.getOrPut(production.left) { mutableSetOf() }
                            .add(it as Terminal to production)
                    }
            }

            while (true) {
                // 如果更新成功则需要再次更新
                if (!map.update(grammar)) {
                    break
                }
            }
            return map.mapValues { it.value.toSet() }
        }

        /**
         * 遍历一遍产生式更新现有的 FirstSet
         * @param grammar 产生式
         * @return 如果更新成功返回 true，否则返回 false
         */
        private fun MutableFirstSetMap.update(grammar: Grammar): Boolean {
            var updated = false
            grammar.forEach { production ->
                // 遍历产生式右边的所有非终结符
                for (rightSymbol in production.right.takeWhile { it is Symbol.NonTerminal }) {
                    val containEmpty = this[rightSymbol]?.let {

                        // 将 First(rightSymbol) 加入到 First(left)
                        updated =
                            updated || this.getOrPut(production.left) { mutableSetOf() }.addAll(it.map { (left, _) ->
                                left to production
                            })

                        // 判断当前的 First 集合是否包括空串
                        it.any { (symbol, _) -> symbol.isEmpty() }
                    } ?: false

                    // 如果不包含空串，则退出
                    if (!containEmpty) {
                        break
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

