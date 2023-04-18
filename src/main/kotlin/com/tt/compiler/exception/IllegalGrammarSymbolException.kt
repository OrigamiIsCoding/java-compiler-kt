package com.tt.compiler.exception

/**
 * @author Origami
 * @date 4/18/2023 8:05 PM
 */
class IllegalGrammarSymbolException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
}