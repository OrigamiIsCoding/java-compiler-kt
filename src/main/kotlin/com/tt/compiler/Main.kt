package com.tt.compiler

import com.tt.compiler.grammar.Production

fun main(args: Array<String>) {
    Production.parse("S -> a S b | a b").forEach(::println)
}