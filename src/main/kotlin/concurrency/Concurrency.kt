package concurrency

import kotlinx.coroutines.CoroutineScope
import reactivity.Gettable
import reactivity.Settable
import java.util.concurrent.locks.ReentrantLock

private typealias ScopedEffect = CoroutineScope.() -> Unit
private typealias ScopedGetter<T> = CoroutineScope.() -> T

private val lock = ReentrantLock()
private var atomicEffect: ScopedEffect? = null

abstract class ScopedRef<T>(
    protected val scope: CoroutineScope,
): Gettable<T>, CoroutineScope by scope {
    private val subscribers = HashSet<ScopedEffect>()

    protected fun <T> track() {
        atomicEffect?.let { effect -> subscribers.add(effect) }
    }

    protected fun <T> trigger() {
        /* TODO: de-duplicate effects */
        subscribers.forEach { effect -> scope.effect() }
    }
}

class ScopedMutableRef<T>(
    scope: CoroutineScope,
    initial: T
): ScopedRef<T>(scope), Settable<T> {
    private var _value = initial

    override operator fun setValue(thisRef: Nothing?, property: Any?, value: T) {
        _value = value
        trigger<T>()
    }

    override operator fun getValue(thisRef: Nothing?, property: Any?): T {
        track<T>()
        return _value
    }
}

class ScopedComputed<T>(
    scope: CoroutineScope,
    private val getter: ScopedGetter<T>
): ScopedRef<T>(scope) {
    override operator fun getValue(thisRef: Nothing?, property: Any?): T {
        track<T>()
        return scope.getter()
    }
}

fun <T> CoroutineScope.ref(initial: T) = ScopedMutableRef(this, initial)

fun <T> CoroutineScope.computed(getter: ScopedGetter<T>) = ScopedComputed(this, getter)

fun CoroutineScope.watchEffect(update: ScopedEffect) {
    lateinit var effect: ScopedEffect
    effect = {
        lock.lock()
        try {
            atomicEffect = effect
            update()
            atomicEffect = null
        } finally {
            lock.unlock()
        }
    }

    effect()
}