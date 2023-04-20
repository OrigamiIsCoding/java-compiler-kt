package com.tt.compiler.grammar

/**
 * @author Origami
 * @date 4/20/2023 12:11 PM
 */
class FollowSet(firstSet: FirstSet, productions: List<Production>) :
    HashMap<Symbol, MutableSet<Symbol>>() {
    init {
        // 往 Follow(Start) 加入 $
        this[Symbol.Start] = mutableSetOf(Symbol.End)
        while (true) {
            if (!this.update(firstSet, productions)) {
                break
            }
        }
    }

    /**
     * 遍历一遍产生式更新现有的 FollowSet
     * @param firstSet FirstSet
     * @param productions 产生式
     * @return 如果更新成功返回 true，否则返回 false
     */
    private fun update(firstSet: FirstSet, productions: List<Production>): Boolean {
        var updated = false
        productions.forEach { (left, rightSymbols) ->
            // 倒序遍历产生式的右部
            for (last in rightSymbols.reversed()) {
                // 如果当前符号是终结符或者和左部相等则退出
                if (last.isTerminal || left == last) {
                    break
                }
                // 将 Follow(left) 加入到 Follow(last)
                this[left]?.let {
                    updated = updated || this.getOrPut(last) { mutableSetOf() }.addAll(it)
                }

                // 如果 First(last) 包含空，则继续循环
                if (firstSet[last]?.any { it.first.isEmpty() } != true) {
                    break
                }
            }

            // 遍历产生式的右部，每次取出相邻两项
            // 左边这一项不能是终结符
            for ((current, next) in rightSymbols.zip(rightSymbols.drop(1))
                .filterNot { it.first.isTerminal }) {

                val currentFollow = this.getOrPut(current) { mutableSetOf() }
                when (next.isTerminal) {
                    // 下一项是终结符，则加入到 Follow(current) 中
                    true -> {
                        updated = updated || currentFollow.add(next)
                    }
                    // 下一项是非终结符，将 {First(next) - 空串} 加入到 Follow(current)
                    false -> {
                        firstSet[next]?.map { it.first }?.let {
                            updated = updated || currentFollow.addAll(it.filterNot(Symbol::isEmpty))
                        }
                    }
                }
            }
        }
        return updated
    }

    override fun toString(): String {
        return "FollowSet {\n" + this.map {
            "\t${it.key} => ${it.value}"
        }.joinToString("\n") + "\n}"
    }
}