package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol

/**
 * @author Origami
 * @date 5/6/2023 8:38 AM
 */
interface LRItem : Iterator<LR0Item> {
    /**
     * 产生式
     */
    val production: Production

    /**
     * 点的位置
     */
    val dot: Int

    /**
     * 小圆点是否可以往下移动
     */
    override fun hasNext(): Boolean {
        return dot < production.right.size
    }

    /**
     * 当前项目在等待的符号
     */
    val wait: Symbol?
        get() {
            if (!hasNext()) {
                return null
            }
            return production.right[dot]
        }

    fun toExpression(): String {
        val rightProductions = production.right.mapIndexed { index, symbol ->
            if (index == dot) {
                "${Symbol.Dot.value} ${symbol.value}"
            } else {
                symbol.value
            }
        }.joinToString(" ") + if (dot == production.right.size) {
            " ${Symbol.Dot.value}"
        } else {
            ""
        }
        return "${production.left.value} ${Production.Separator} $rightProductions"
    }

    companion object {
        /**
         * 解析带有圆点的产生式
         * @param line 表达式
         * @return Pair(忽略圆点的产生式, 点的位置)
         */
        fun parseProduction(line: String): Pair<Production, Int> {
            val (left, right) = Production.parse(line).first()
            val i = right.indexOf(Symbol.Dot)
            check(i != -1 && right.count { it == Symbol.Dot } == 1) {
                "项目产生式中必须有且仅有一个 ${Symbol.Dot.value} 符号"
            }
            return Pair(Production(left, right.filter { it != Symbol.Dot }), i)
        }
    }
}