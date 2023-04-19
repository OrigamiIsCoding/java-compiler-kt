package com.tt.compiler.grammar

import com.tt.compiler.exception.IllegalGrammarSymbolException

/**
 * @author Origami
 * @date 4/18/2023 7:59 PM
 */
data class Symbol(
    val value: String,
    val isTerminal: Boolean
) {
    companion object {
        /**
         * 语法的结尾符号
         */
        val End = Symbol("$", true)

        /**
         * 空串
         */
        val Empty = Symbol("ε", true)

        /**
         * 开始符号
         */
        val Start = Symbol("S'", false)

        fun from(value: String): Symbol {
            return value.trim().ifBlank {
                throw IllegalGrammarSymbolException("终结符和非终结符不能为空")
            }.let {
                when (it) {
                    End.value -> End
                    Empty.value -> Empty
                    Start.value -> Start
                    else -> Symbol(value, checkIsTerminal(it))
                }
            }
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
    }

    /**
     * 是否是结尾符号
     * @return true: 是; false: 否
     */
    fun isEmpty(): Boolean {
        return this == Empty
    }

    override fun toString(): String {
        return "Symbol { $value }"
    }
}
