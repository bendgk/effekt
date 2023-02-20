import reactivity.computed
import reactivity.ref
import reactivity.watchEffect

fun main() {
    var a by ref(0)
    val b by computed { a + 1 }
    val c by computed { b + 1 }

    watchEffect {
        println("b: $b")
    }

    watchEffect {
        println("c: $c")
    }

    a += 1
    a += 1
}