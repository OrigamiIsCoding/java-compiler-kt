package com.tt.compiler.grammar

/**
 * @author Origami
 * @date 4/23/2023 1:07 PM
 */
class Grammar(productions: List<Production>, val isExtended: Boolean = false) : List<Production> by productions {
    init {
        check(this.isEmpty() || this.any { it.left.isStart() }) {
            "文法的左部必须存在起始符号 ${Symbol.Start.value}"
        }
    }

    fun toExtended(): Grammar {
        // 检查是否存在拓广文法的起始符号
        if (this.any { it.left == Symbol.ExtendedStart || it.right.contains(Symbol.ExtendedStart) }) {
            throw IllegalStateException("转为扩广文法冲突，原始文法中包含 ${Symbol.ExtendedStart.value}(拓广文法的起始符号)")
        }
        return Grammar(listOf(Production.ExtendedProduction) + this, true)
    }

    companion object {
        val Empty = Grammar(emptyList())

        fun parse(inputGrammar: String): Grammar {
            return Grammar(inputGrammar.split("\n").flatMap(Production::parse))
        }
    }
}