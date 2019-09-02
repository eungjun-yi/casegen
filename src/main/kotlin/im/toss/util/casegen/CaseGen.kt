package im.toss.util.casegen

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

interface SpecHelper {
    fun <T> anyOf(values: Iterable<T>): T
    fun <T> any(klass: KClass<*>, isNullable: Boolean): T
}

@UseExperimental(ExperimentalStdlibApi::class)
inline fun <reified U> SpecHelper.any(): U = this.any(U::class, typeOf<U>().isMarkedNullable)
inline fun <reified U> SpecHelper.anyOf(vararg values:U) = this.anyOf(values.toList())

internal class Permutator<T>(val spec: SpecHelper.() -> T): SpecHelper {
    private val parameters = mutableListOf<Iterable<*>>()
    override fun <U> any(klass: KClass<*>, isNullable: Boolean): U {
        val elements = extractElements<U>(klass, isNullable)
        parameters.add(elements)
        return elements.first()
    }

    override fun <U> anyOf(values: Iterable<U>): U {
        parameters.add(values)
        return values.first()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <U> extractElements(klass: KClass<*>, isNullable: Boolean): Iterable<U> {
        val result = when(val elements = klass.java.enumConstants) {
            null -> if (klass.isSubclassOf(Boolean::class)) {
                listOf(false as U, true as U)
            } else {
                throw UnsupportedOperationException()
            }
            else -> elements.map { it as U }.toList()
        }
        return if (isNullable) {
            listOf(null as U) + result
        } else {
            result
        }
    }

    private fun generate(arguments: List<Any?>): Sequence<T> = sequence {
        if (arguments.size >= parameters.size) {
            val args = Arguments(arguments)
            yield (spec(args))
        } else {
            for (i in parameters[arguments.size]) {
                yieldAll(generate(arguments + listOf(i)))
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Arguments(arguments: List<Any?>): SpecHelper {
        private val iterator = arguments.iterator()

        override fun <T> any(klass: KClass<*>, isNullable: Boolean): T {
            return iterator.next() as T
        }

        override fun <T> anyOf(values: Iterable<T>): T {
            return iterator.next() as T
        }
    }

    fun generate(): Sequence<T> {
        spec(this)
        return generate(emptyList())
    }
}

fun <T> cases(spec: SpecHelper.() -> T): Sequence<T> {
    return Permutator(spec).generate()
}