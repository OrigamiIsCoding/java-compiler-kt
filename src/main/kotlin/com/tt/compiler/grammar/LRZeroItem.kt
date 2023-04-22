package com.tt.compiler.grammar

/**
 * LR(0) 项目
 * @author Origami
 * @date 4/22/2023 7:49 PM
 */
data class LRZeroItem(
    /**
     * 产生式
     */
    val production: Production,
    /**
     * 点的位置
     */
    val dotIndex: Int = 0
) : Iterator<LRZeroItem> {

    override fun toString(): String {
        return "LRZeroItem { ${toExpression()} }"
    }

    fun toExpression(): String {
        val rightProductions = production.right.mapIndexed { index, symbol ->
            if (index == dotIndex) {
                "· ${symbol.value}"
            } else {
                symbol.value
            }
        }.joinToString(" ") + if (dotIndex == production.right.size) {
            " ·"
        } else {
            ""
        }
        return "${production.left.value} ${Production.Separator} $rightProductions"
    }

    override fun hasNext(): Boolean {
        return dotIndex < production.right.size
    }

    override fun next(): LRZeroItem {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        return LRZeroItem(production, dotIndex + 1)
    }
}