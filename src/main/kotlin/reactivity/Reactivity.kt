package reactivity

import java.util.concurrent.locks.ReentrantLock

private typealias Effect = () -> Unit
private typealias Getter<T> = () -> T

/**
 * the current locking mechanism only allows one effect to be running at a time
 */
private val lock = ReentrantLock()
private var activeEffect: Effect? = null

/**
 * represents a delegate getter
 */
private interface Gettable<T> {
    operator fun getValue(thisRef: Nothing?, property: Any?): T
}

/**
 * represents a delegate setter
 */
private interface Settable<T> {
    operator fun setValue(thisRef: Nothing?, property: Any?, value: T)
}

/**
 * the most primitive reactive object is a ref, it holds a single value that can only be read.
 * a ref is a dependency that can be subscribed to. The ref will notify all subscribers when it changes.
 */
abstract class Ref<T>: Gettable<T> { val subscribers = HashSet<Effect>() }

/**
 * a mutable ref is a ref that can be read and written to
 * @param initial the initial value of the ref
 * @see Ref
 */
class MutableRef<T>(initial: T): Ref<T>(), Settable<T> {
    private var _value = initial
    override operator fun setValue(thisRef: Nothing?, property: Any?, value: T) {
        _value = value
        trigger(this)
    }

    override operator fun getValue(thisRef: Nothing?, property: Any?): T {
        track(this)
        return _value
    }
}

/**
 * a computed reactive object is a ref that is computed by a Getter
 * @param getter the getter to compute on
 * @see Getter
 * TODO: add caching of computed values
 */
class Computed<T>(
    private val getter: Getter<T>
) : Ref<T>() {
    override operator fun getValue(thisRef: Nothing?, property: Any?): T {
        track(this)
        return getter()
    }
}

/**
 * Creates a computed reactive object
 * @param getter the getter to compute on
 */
fun <T> computed(getter: Getter<T>) = Computed(getter)

/**
 * Creates a ref reactive object
 * @param initial the initial value of the ref
 */
fun <T> ref(initial: T) = MutableRef(initial)

/**
 * Watches dependencies and runs the effect when they change.
 * @param update The effect that is run when the dependencies change.
 */
fun watchEffect(update: Effect) {
    lateinit var effect: Effect
    effect = {
        lock.lock()
        try {
            activeEffect = effect
            update()
            activeEffect = null
        } finally {
            lock.unlock()
        }
    }

    effect()
}

fun watch(update: Effect) {
    TODO("not implemented")
}

private fun <T> track(target: Ref<T>) {
    activeEffect?.let { target.subscribers.add(it) }
}

private fun <T> trigger(target: Ref<T>) {
    /* TODO: de-duplicate effects */
    target.subscribers.forEach { it() }
}

