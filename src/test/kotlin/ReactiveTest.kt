import reactivity.computed
import reactivity.ref
import reactivity.watchEffect

fun main() {
    val a = ref(2)
    val b = ref(3)
    val c = computed { a.value + b.value }

    watchEffect {
        println("c: ${c.value}")
    }

    a.value = 10
    b.value = 1000
    b.value = -10
}