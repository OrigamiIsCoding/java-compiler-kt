package com.tt.compiler.grammar

import com.tt.compiler.exception.IllegalGrammarSymbolException

/**
 * @author Origami
 * @date 4/18/2023 8:12 PM
 */
data class Production(
    val left: NonTerminal,
    val right: List<Symbol>
) {
    companion object {
        val ExtendedProduction = Production(
            left = Symbol.ExtendedStart,
            right = listOf(Symbol.Start)
        )
        const val Separator = "->"
        private const val Or = "|"

        /**
         * 解析产生式
         * @param line 产生式
         */
        fun parse(line: String): List<Production> {
            val tokens = line.split(" ").filter(String::isNotBlank)
            if (tokens.size < 3 && Separator != tokens[1]) {
                throw IllegalGrammarSymbolException("产生式格式错误")
            }

            // 获取产生式的左部
            val left = tokens.first()
                .let(Symbol::from)
                .takeIf { it is NonTerminal } ?: throw IllegalGrammarSymbolException("产生式左部必须是非终结符")
            // 获取产生式的右部，然后按照 | 分割，最后按照空格分割
            return tokens.drop(2)
                .joinToString(" ")
                .split(Or)
                .map {
                    Production(
                        left = left as NonTerminal,
                        right = it.split(" ").filter(String::isNotBlank).map(Symbol::from).ifEmpty {
                            throw IllegalGrammarSymbolException("产生式右部不能为空")
                        }
                    )
                }
        }
    }

    override fun toString(): String {
        return "Production { ${toExpression()} }"
    }

    fun toExpression(): String {
        return "${left.value} $Separator ${right.map(Symbol::value).joinToString(" ")}"
    }

}