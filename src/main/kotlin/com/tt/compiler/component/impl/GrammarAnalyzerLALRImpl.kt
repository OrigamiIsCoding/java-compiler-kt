package com.tt.compiler.component.impl

import com.tt.compiler.grammar.Grammar
import com.tt.compiler.grammar.lr.LRParseTable

/**
 * @author Origami
 * @date 5/6/2023 8:56 AM
 */
class GrammarAnalyzerLALRImpl(
    override val grammar: Grammar,
    override val parseTable: LRParseTable
) : GrammarAnalyzerLR