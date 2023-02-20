<div align="center">
  <picture>
    <img alt="effekt logo" src="https://raw.githubusercontent.com/bendgk/effekt/main/res/logo2x.png">
  </picture>
</div>

[![Kotlin](https://img.shields.io/badge/kotlin-1.7.20-blue.svg?logo=kotlin)](http://kotlinlang.org)

# effekt
Kotlin Reactive Framework inspired by [Vue.js](https://vuejs.org/guide/extras/reactivity-in-depth.html)

# Usage
effekt brings the following reactive primitives from vue to kotlin:
* [ref](https://vuejs.org/api/reactivity-core.html#ref)
* [computed](https://vuejs.org/api/reactivity-core.html#computed)
* [watchEffect](https://vuejs.org/api/reactivity-core.html#watcheffect)

# Example
```kotlin
val a = ref(2)
val b = ref(3)
val c = computed { a.value + b.value }

watchEffect {
    println("c: ${c.value}")
}

a.value = 10
b.value = 1000
b.value = -10
```

Output:
```bash
> c: 5
> c: 13
> c: 1010
> c: 0
```
