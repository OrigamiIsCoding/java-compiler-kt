package com.tt.compiler.grammar.lr

import com.tt.compiler.grammar.Production

/**
 * LR(0) 项目
 * @author Origami
 * @date 4/22/2023 7:49 PM
 */
data class LR0Item(
    /**
     * 产生式
     */
    override val production: Production,
    /**
     * 点的位置
     */
    override val dot: Int = 0
) : LRItem {

    companion object {
        /**
         * 开始的项目就是扩展文法加入的产生式
         */
        val Start = LR0Item(Production.ExtendedProduction)

        /**
         * 接受的项目就是扩展文法往下移动一次
         */
        val Accept = Start.next()

        fun parse(line: String): LR0Item {
            val (production, dot) = LRItem.parseProduction(line)
            return LR0Item(production, dot)
        }
    }

    /**
     * 移动到下一个位置，并返回
     */
    override fun next(): LR0Item {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        return LR0Item(production, dot + 1)
    }

    override fun toString(): String {
        return "LR(0) { ${toExpression()} }"
    }
}
