package com.tt.compiler.grammar

/**
 * @author Origami
 * @date 4/19/2023 8:58 AM
 */
class FirstSet(productions: List<Production>) :
    HashMap<Symbol.NonTerminal, MutableSet<Pair<Symbol.Terminal, Production>>>() {
    init {
        // 第一次先将 right 的第一个为终结符的加入到 First(left) 中
        productions.forEach { production ->
            production.right.take(1)
                .firstOrNull { it is Symbol.Terminal }?.let {
                    this.getOrPut(production.left) { mutableSetOf() }
                        .add(it as Symbol.Terminal to production)
                }
        }

        while (true) {
            // 如果更新成功则需要再次更新
            if (!update(productions)) {
                break
            }
        }
    }

    /**
     * 遍历一遍产生式更新现有的 FirstSet
     * @param productions 产生式
     * @return 如果更新成功返回 true，否则返回 false
     */
    private fun update(productions: List<Production>): Boolean {
        var updated = false
        productions.forEach { production ->
            // 遍历产生式右边的所有非终结符
            for (rightSymbol in production.right.takeWhile { it is Symbol.NonTerminal }) {
                val containEmpty = this[rightSymbol]?.let {

                    // 将 First(rightSymbol) 加入到 First(left)
                    updated = updated || this.getOrPut(production.left) { mutableSetOf() }.addAll(it.map { (left, _) ->
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

    override fun toString(): String {
        return "FirstSet {\n" + this.map { it ->
            "\t${it.key} => ${it.value.map { it.first }}"
        }.joinToString("\n") + "\n}"
    }
}