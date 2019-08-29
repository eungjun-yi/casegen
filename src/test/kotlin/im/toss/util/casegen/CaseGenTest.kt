package im.toss.util.casegen

import im.toss.test.equalsTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CaseGenTest {
    @Test
    fun test1() {
        data class Foo(val a: Int, val b: Boolean?, val c: String)

        cases {
            Foo(anyOf(1, 2, 3), anyOf(true, null), anyOf("hello", "world", "haha"))
        }.toSet().equalsTo(
            setOf(
                Foo(a=1, b=true, c="hello"),
                Foo(a=1, b=true, c="world"),
                Foo(a=1, b=true, c="haha"),
                Foo(a=1, b=null, c="hello"),
                Foo(a=1, b=null, c="world"),
                Foo(a=1, b=null, c="haha"),
                Foo(a=2, b=true, c="hello"),
                Foo(a=2, b=true, c="world"),
                Foo(a=2, b=true, c="haha"),
                Foo(a=2, b=null, c="hello"),
                Foo(a=2, b=null, c="world"),
                Foo(a=2, b=null, c="haha"),
                Foo(a=3, b=true, c="hello"),
                Foo(a=3, b=true, c="world"),
                Foo(a=3, b=true, c="haha"),
                Foo(a=3, b=null, c="hello"),
                Foo(a=3, b=null, c="world"),
                Foo(a=3, b=null, c="haha")
            )
        )
    }

    enum class Bar {
        A, B
    }

    @Test
    fun `중복되는 경우가 없어야 한다`() {
        data class Foo(val a: Bar)
        val cases = cases {
            Foo(any())
        }.toList()
        cases.size.equalsTo(cases.toSet().size)
    }

    @Test
    fun testEnum() {
        data class Foo(val a: Bar)
        cases {
            Foo(any())
        }.toSet().equalsTo(
            setOf(Foo(Bar.A), Foo(Bar.B))
        )
    }

    @Test
    fun testBoolean() {
        data class Foo(val a: Boolean)
        cases {
            Foo(any())
        }.toSet().equalsTo(
            setOf(Foo(false), Foo(true))
        )
    }

    @Test
    fun testUnsupportedType() {
        data class Foo(val a: Long)
        assertThrows<UnsupportedOperationException> {
            cases {
                Foo(any())
            }
        }
    }
}
