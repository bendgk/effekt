package reactivity

import java.util.*
import java.util.concurrent.atomic.AtomicReference

typealias Effect = () -> Unit
typealias Getter<T> = () -> T
typealias Setter<T> = (T) -> Unit

private val atomicEffect = AtomicReference<Effect?>(null)

/**
 * Creates a computed reactive object
 * @param getter the getter to compute on
 */
fun <T> computed(getter: Getter<T>) = Computed(getter)

/**
 * Creates a ref reactive object
 * @param initial the initial value of the ref
 */
fun <T> ref(initial: T) = RefImpl(initial)

/**
 * Watches dependencies and runs the effect when they change.
 * @param update The effect that is run when the dependencies change.
 */
fun watchEffect(update: Effect) {
    lateinit var effect: Effect
    effect = {
        atomicEffect.set(effect)
        update()
        atomicEffect.set(null)
    }

    effect()
}

/**
 * A ref is the most primitive reactive object. it tracks a single value.
 */
abstract class Ref<T> {
    val subscribers = HashSet<Effect>()
    abstract var value: T
    abstract operator fun invoke(): T
    abstract operator fun invoke(value: T)
}

/**
 * A computed is a reactive object that is derived from its dependencies.
 * TODO: Cache computed values until deps change
 */
class Computed<T>(private val getter: Getter<T>): Ref<T>() {
    override var value: T
        get() {
            track(this)
            return getter()
        }
        set(_) {
            throw UnsupportedOperationException("Cannot set value of computed")
        }

    override operator fun invoke() = value
    override fun invoke(value: T) {
        throw UnsupportedOperationException("Cannot set value of computed")
    }
}

/**
 * A ref is a reactive object that can be used to track a single value.
 */
class RefImpl<T>(initial: T): Ref<T>() {
    private val _value = AtomicReference(initial)
    override var value: T
        get() {
            track(this)
            return _value.get()
        }
        set(value) {
            _value.set(value)
            trigger(this)
        }

    override fun invoke(): T { return value }

    override operator fun invoke(value: T) {
        this.value = value
    }
}

private fun <T> track(target: Ref<T>) {
    try {
        if (atomicEffect.get() != null) {
            target.subscribers.add(atomicEffect.get()!!)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun <T> trigger(target: Ref<T>) {
    /* TODO: de-duplicate effects */
    try {
        target.subscribers.forEach { it() }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

