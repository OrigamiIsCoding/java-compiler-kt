package com.tt.compiler.grammar

import com.tt.compiler.exception.IllegalGrammarSymbolException

/**
 * @author Origami
 * @date 4/18/2023 7:59 PM
 */
typealias Terminal = Symbol.Terminal
typealias NonTerminal = Symbol.NonTerminal

sealed class Symbol(
    val value: String
) {
    class Terminal(value: String) : Symbol(value) {
        override fun toString(): String {
            return "Terminal { $value }"
        }
    }

    class NonTerminal(value: String) : Symbol(value) {
        override fun toString(): String {
            return "NonTerminal { $value }"
        }
    }

    companion object {
        /**
         * 语法的结尾符号
         */
        val End = Terminal("$")

        /**
         * 空串
         */
        val Empty = Terminal("ε")

        /**
         * 开始符号
         */
        val Start = NonTerminal("S")

        /**
         * 拓广文法的开始符号
         */
        val ExtendedStart = NonTerminal("S'")

        fun from(value: String): Symbol {
            return value.trim().ifBlank {
                throw IllegalGrammarSymbolException("终结符和非终结符不能为空")
            }.let {
                when (it) {
                    End.value -> End
                    Empty.value -> Empty
                    Start.value -> Start
                    ExtendedStart.value -> ExtendedStart
                    else -> if (checkIsTerminal(it)) Terminal(it) else NonTerminal(it)
                }
            }
        }

        fun terminal(value: String): Terminal {
            return Terminal(value.notBlank().takeIf { checkIsTerminal(it) }
                ?: throw IllegalGrammarSymbolException("$value 不满足终结符要求"))
        }

        fun nonTerminal(value: String): NonTerminal {
            return NonTerminal(value.notBlank().takeIf { !checkIsTerminal(it) }
                ?: throw IllegalGrammarSymbolException("$value 不满足非终结符要求"))
        }

        /**
         * 检查是否是终结符
         * @param value 符号
         * @return true: 终结符; false: 非终结符
         */
        private fun checkIsTerminal(value: String): Boolean {
            val lower = value.chars().anyMatch(Character::isLowerCase)
            val upper = value.chars().anyMatch(Character::isUpperCase)
            if (lower && upper) {
                throw IllegalGrammarSymbolException("终结符和非终结符不能同时包含大写和小写字母")
            }
            return !upper
        }

        private fun String.notBlank(): String {
            return this.trim().ifBlank {
                throw IllegalGrammarSymbolException("终结符和非终结符不能为空")
            }
        }
    }

    /**
     * 是否是结尾符号
     * @return true: 是; false: 否
     */
    fun isEmpty(): Boolean {
        return this == Empty
    }

    fun isStart(): Boolean {
        return this == Start
    }

    override fun toString(): String {
        return "Symbol { $value }"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Symbol) return false

        return value == other.value && this::class == other::class
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
