import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import reactivity.computed
import reactivity.ref
import reactivity.watchEffect
import kotlin.time.Duration.Companion.seconds

fun main() {
    runBlocking {
        var quantity by ref(1000)
        var price by ref(2.00)
        val revenue by computed { "$%.2f".format(quantity * price) }

        watchEffect {
            println("revenue: $revenue")
        }

        quantity += 500
        price = 1.25
        delay(1.seconds)
        quantity += 500
        price = 1.25

        launch {
            watchEffect {
                println("new price: ${"$%.2f".format(price)}")
            }

            while (true) {
                delay(1.seconds)
                price += .1
            }

        }
    }
}