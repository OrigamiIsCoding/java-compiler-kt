package com.tt.compiler.component

import com.tt.compiler.grammar.LRZeroItem
import com.tt.compiler.grammar.Production
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Origami
 * @date 4/22/2023 7:55 PM
 */
class GrammarAnalyzerSLRImplTest {
    @Test
    fun testLRZeroItem() {
        val production = Production.parse("S -> T E").first()
        val lrZeroItem = LRZeroItem(production)
        assertEquals("S -> · T E", lrZeroItem.toExpression())
        assertEquals("S -> T · E", lrZeroItem.next().toExpression())
        assertEquals("S -> T E ·", lrZeroItem.next().next().toExpression())
        assertThrows<NoSuchElementException> {
            lrZeroItem.next().next().next()
        }
    }
}