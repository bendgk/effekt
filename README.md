<div align="center">
    <img alt="EffeKt logo" src="https://raw.githubusercontent.com/bendgk/effekt/main/res/logo1.5x.png">
</div>

[![Kotlin](https://img.shields.io/badge/kotlin-1.7.20-blue.svg?logo=kotlin)](http://kotlinlang.org)

# EffeKt
Kotlin reactive library inspired by [Vue.js](https://vuejs.org/guide/extras/reactivity-in-depth.html). It seeks to provide reactive primitives to kotlin for building functionally coupled systems.

# Usage
EffeKt brings the following reactive primitives from vue to kotlin:
* [ref](https://vuejs.org/api/reactivity-core.html#ref)
* [computed](https://vuejs.org/api/reactivity-core.html#computed)
* [watchEffect](https://vuejs.org/api/reactivity-core.html#watcheffect)

### ref
A ref is a reactive primitive that can be read and written to.  Ref's store a single value and a list of subscribers.  When a ref is used as a dependency of another primitive (such as `computed` or `watchEffect`), that primitive is added as a subscriber to that ref.
when a ref is updated we need to update all the subscribers too.

### computed
A computed is a reactive primitive that can only be read. Whenever a computed value is read its value is recomputed according to its dependencies.

### watchEffect
watchEffect is a reactive helper function that can watch dependencies for changes.  It runs the given side effect when a dependency change occurs.

# Example
```kotlin
var price by ref(2.00)
var quantity by ref(1000)
val revenue by computed { price * quantity }

watchEffect {
    println("revenue: $revenue")
}

price /= 2
price *= 10
quantity += 500
```

Output:
```bash
> revenue: 2000.0
> revenue: 1000.0
> revenue: 10000.0
> revenue: 15000.0
```

