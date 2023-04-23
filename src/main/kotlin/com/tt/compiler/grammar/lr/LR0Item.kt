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

    override fun toString(): String {
        return "LRZeroItem { ${toExpression()} }"
    }

    fun toExpression(): String {
        val rightProductions = production.right.mapIndexed { index, symbol ->
            if (index == dot) {
                "· ${symbol.value}"
            } else {
                symbol.value
            }
        }.joinToString(" ") + if (dot == production.right.size) {
            " ·"
        } else {
            ""
        }
        return "${production.left.value} ${Production.Separator} $rightProductions"
    }
}