package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.Production
import com.tt.compiler.grammar.Symbol

/**
 * LR(0) 项目
 * @author Origami
 * @date 4/22/2023 7:49 PM
 */
data class LR0Item(
    /**
     * 产生式
     */
    val production: Production,
    /**
     * 点的位置
     */
    val dot: Int = 0
) : Iterator<LR0Item> {

    override fun hasNext(): Boolean {
        return dot < production.right.size
    }

    override fun next(): LR0Item {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        return LR0Item(production, dot + 1)
    }

    fun acceptNext(): Pair<Symbol, LR0Item> {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        return Pair(production.right[dot], LR0Item(production, dot + 1))
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

    companion object {
        val Start = LR0Item(Production.ExtendedProduction)

        fun parse(line: String): LR0Item {
            val (left, right) = Production.parse(line).first()
            val i = right.indexOf(Symbol.Dot)
            check(i != -1 && right.count { it == Symbol.Dot } == 1) {
                "LR(0) 项目产生式中必须有且仅有一个 ${Symbol.Dot.value} 符号"
            }
            return LR0Item(Production(left, right.filter { it != Symbol.Dot }), i)
        }
    }

    override fun toString(): String {
        return "LR(0)Item { ${toExpression()} }"
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
}