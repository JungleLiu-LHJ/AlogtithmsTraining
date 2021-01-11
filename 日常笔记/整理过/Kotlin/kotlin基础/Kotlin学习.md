# Kotlin



## 变量、函数和类型

>https://kaixue.io/kotlin-basic-1/

### 变量

``kotlin`声明变量的时候要初始化，必须要有默认值

如果一开始没法初始化，后面初始化，则可以用

```kotlin
lateinit var view: View
```

对于可以为空的属性，则可以

```kotlin
var name: String? = null
println(name?.length)
```

* 变量需要手动初始化，所以不初始化会报错

* 变量默认为非空，所以初始化时赋值为null也会报错

* 变量用？号设置为可空，使用的时候又报错

  >关注的都是使用的时候

var:variable  val:value

**Kotlin的get/set写法**

```kotlin
class User {
    var name = "Mike"
        get() {
            return field 
        }
        set(value) {
            field = value
        }
}
```

### 类型

`Kotlin`在语言层面没有基本类型



函数如果不加可见性修饰符的话，默认的可见范围和变量一样也是 public 的，但有一种情况例外，这里简单提一下，就是遇到了 `override` 关键字的时候，



val 所声明的只读变量，在取值的时候仍然可能被修改，这也是和 Java 里的 final 的不同之处。



Kotlin 里，Int 是否装箱根据场合来定：

```kotlin
var a: Int = 1 // unbox
var b: Int? = 2 // box
var list: List<Int> = listOf(1, 2) // box
```

不装箱的情况：

* 不可控类型
* 使用IntArray、FloatArray等



### 类和对象

Kotlin的类默认是public的

**构造函数** 写法

```kotlin
class MainActivity : AppCompatActivity {
    constructor() {
    }
}
```

 Kotlin 里的类默认是 final 的，而 Java 里只有加了 `final` 关键字的类才是 final 的。

要解除这个必须用`open`，才可以被继承。

如果要关闭 `override` 的遗传性，只需要这样即可：

```kotlin
open class MainActivity : AppCompatActivity() {
    // 👇加了 final 关键字，作用和 Java 里面一样，关闭了 override 的遗传性
    final override fun onCreate(savedInstanceState: Bundle?) {
        ...
    }
}
```

**类型转换**：

```kotlin
🏝️
fun main() {
    var activity: Activity = NewActivity()
    // 👇'(activity as? NewActivity)' 之后是一个可空类型的对象，所以，需要使用 '?.' 来调用
    (activity as? NewActivity)?.action()
}
```

