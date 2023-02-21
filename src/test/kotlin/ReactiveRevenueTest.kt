import reactivity.computed
import reactivity.ref
import reactivity.watchEffect

fun main() {
    var price by ref(2.00)
    var quantity by ref(1000)
    val revenue by computed { price * quantity }

    watchEffect {
        println("revenue: $revenue")
    }

    price /= 2
    price *= 10
    quantity += 500
}