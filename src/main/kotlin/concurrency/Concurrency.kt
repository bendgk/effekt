package concurrency

import kotlinx.coroutines.CoroutineScope
import reactivity.*
import java.util.concurrent.locks.ReentrantLock

private typealias ScopedEffect = CoroutineScope.() -> Unit
private typealias ScopedGetter<T> = CoroutineScope.() -> T

private val lock = ReentrantLock()
private var atomicEffect: ScopedEffect? = null

/**
 * ScopedRef provides a CoroutineScope to all reactive primitives
 * the most primitive reactive object is a ref, it holds a single value that can only be read.
 * a ref is a dependency that can be subscribed to. The ref will notify all subscribers when it changes.
 * @param scope the coroutine scope to provide
 * @see Ref
 * @see CoroutineScope
 */
abstract class ScopedRef<T>(
    protected val scope: CoroutineScope,
): Gettable<T> {
    private val subscribers = HashSet<ScopedEffect>()

    protected fun <T> track() {
        atomicEffect?.let { effect -> subscribers.add(effect) }
    }

    protected fun <T> trigger() {
        /* TODO: de-duplicate effects */
        subscribers.forEach { effect -> scope.effect() }
    }
}

/**
 * ScopedMutableRef is provided a CoroutineScope by ScopedRef
 * a mutable ref is a ref that can be read and written to
 * @param scope the coroutine scope to provide
 * @param initial the initial value of the ref
 * @see ScopedRef
 */
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

/**
 * ScopedComputed is provided a CoroutineScope by ScopeRef
 * a computed reactive object is a ref that is computed by a Getter
 * @param scope the coroutine scope to provide
 * @param getter the getter to compute on
 * @see ScopedGetter
 * @see ScopedRef
 * TODO: add caching of computed values
 */
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