

# Kotlin 与 Java 不同



##  `Constructor`

```kotlin
class User {
    val id: Int
    val name: String
         👇
    constructor(id: Int, name: String) {
 //👆 没有 public
        this.id = id
        this.name = name
    }
}
```

- Java 中构造器和类同名，Kotlin 中使用 `constructor` 表示。
- Kotlin 中构造器没有 public 修饰，因为默认可见性就是公开的（关于可见性修饰符这里先不展开，后面会讲到）



## `init`

Java:

```java
public class User {
   👇
    {
        // 初始化代码块，先于下面的构造器执行
    }
    public User() {
    }
}
```

Kotlin:

```kotlin

class User {
    
    init {
        // 初始化代码块，先于下面的构造器执行
    }
    constructor() {
    }
}
```

> Kotlin 的 init 代码块和 Java 一样，都在实例化时执行，并且执行顺序都在构造器之前。

## `final`与`val`

不同：val的`get()`可以更改值。



## `static`与`Object`

Kotlin的Object

```kotlin
object Sample {
    val name = "A name"
}
```

属于关键字，意思是：**创建一个类，并且创建一个这个类的对象**。

Kotlin实现单例的方式：

```kotlin
object A {
    val number: Int = 1
    fun method() {
        println("A.method()")
    }
}
```

如果想让内部变成静态的

```kotlin
class A {
                // 👇 B 没了
    companion object {
        var c: Int = 0
    }
}
```

**更好的做法：顶层声明，声明的隶属`package`**

工具类：可以创建一个类，全部写成top-level的函数就可以。

>能写在top-level就写在top-level

### **匿名类**：
java:

```java
ViewPager.SimpleOnPageChangeListener listener = new ViewPager.SimpleOnPageChangeListener() {
    @Override // 
    public void onPageSelected(int position) {
        // override
    }
};
```

Kotlin:

```kotlin
val listener = object: ViewPager.SimpleOnPageChangeListener() {
    override fun onPageSelected(position: Int) {
        // override
    }
}
```

和 Java 创建匿名类的方式很相似，只不过把 `new` 换成了 `object:`：

- Java 中 `new` 用来创建一个匿名类的对象
- Kotlin 中 `object:` 也可以用来创建匿名类的对象

这里的 `new` 和 `object:` 修饰的都是接口或者抽象类



**对比**：

那在实际使用中，在 `object`、`companion object` 和 top-level 中该选择哪一个呢？简单来说按照下面这两个原则判断：

>- 如果想写工具类的功能，直接创建文件，写 top-level「顶层」函数。
>- 如果需要继承别的类或者实现接口，就用 `object` 或 `companion object`。



### 常量

Java：

```java
public class Sample {

    public static final int CONST_NUMBER = 1;
}
```

kotlin：

```kotlin
class Sample {
    companion object {
         👇                  // 👇
        const val CONST_NUMBER = 1
    }
}

const val CONST_SECOND_NUMBER = 2
```

发现不同点有：

- Kotlin 的常量必须声明在对象（包括伴生对象）或者「top-level 顶层」中，因为常量是静态的。
- Kotlin 新增了修饰常量的 `const` 关键字。
- Kotlin 中只有基本类型和 String 类型可以声明成常量

> 原因是 Kotlin 中的常量指的是 「compile-time constant 编译时常量」，它的意思是「编译器在编译的时候就知道这个东西在每个调用处的实际值」，因此可以在编译时直接把这个值硬编码到代码里使用的地方。



## 数组和集合

### 数组

可以看到 Kotlin 中的数组是一个拥有泛型的类，创建函数也是泛型函数，和集合数据类型一样

kotlin:

```kotlin
val strs: Array<String> = arrayOf("a", "b", "c")
```

不支持协变

Kotlin 的数组编译成字节码时使用的仍然是 Java 的数组，但在语言层面是泛型实现，这样会失去协变 (covariance) 特性，就是子类数组对象不能赋值给父类的数组变量：

```kotlin
val strs: Array<String> = arrayOf("a", "b", "c")
                  👆
val anys: Array<Any> = strs // compile-error: Type mismatch
```

### 集合

#### List

```kotlin
val strList = listOf("a", "b", "c")
```

不同点：

首先能看到的是 Kotlin 中创建一个 `List` 特别的简单，有点像创建数组的代码。而且 Kotlin 中的 `List` 多了一个特性：支持 covariant（协变）。也就是说，可以把子类的 `List` 赋值给父类的 `List` 变量：

#### Set

```kotlin
val strSet = setOf("a", "b", "c")
```

支持covariant

#### Map

```kotlin
val map = mapOf("key1" to 1, "key2" to 2, "key3" to 3, "key4" to 3)
```

#### 可变集合/不可变集合

- `listOf()` 创建不可变的 `List`，`mutableListOf()` 创建可变的 `List`。
- `setOf()` 创建不可变的 `Set`，`mutableSetOf()` 创建可变的 `Set`。
- `mapOf()` 创建不可变的 `Map`，`mutableMapOf()` 创建可变的 `Map`

不可变是指：集合的size、元素值都不可变



## 可见性修饰符

Kotlin 中有四种可见性修饰符：

- `public`：公开，可见性最大，哪里都可以引用。
- `private`：私有，可见性最小，根据声明位置不同可分为类中可见和文件中可见。
- `protected`：保护，相当于 `private` + 子类可见。
- `internal`：内部，仅对 module 内可见。

## 操作符

### `!!` 操作符

第三种选择是为 NPE 爱好者准备的：非空断言运算符（`!!`）将任何值转换为非空类型，若该值为空则抛出异常。我们可以写 `b!!` ，这会返回一个非空的 `b` 值 （例如：在我们例子中的 `String`）或者如果 `b` 为空，就会抛出一个 `NPE` 异常：

```kotlin
val l = b!!.length
```

因此，如果你想要一个 NPE，你可以得到它，但是你必须显式要求它，否则它不会不期而至。



## 修饰符

### 类型别名(`typealias`)

类型别名为现有类型提供替代名称。 如果类型名称太长，你可以另外引入较短的名称，并使用新的名称替代原类型名。

``` kotlin
class A {
    inner class Inner
}
class B {
    inner class Inner
}

typealias AInner = A.Inner
typealias BInner = B.Inner
```



### 内联类（`inline`）

``` kotlin
inline class Password(val value: String)
// 不存在 'Password' 类的真实实例对象
// 在运行时，'securePassword' 仅仅包含 'String'
val securePassword = Password("Don't try this in production")
```

这就是内联类的主要特性，它灵感来源于 “inline” 这个名称：类的数据被 “内联”到该类使用的地方

>有时候，业务逻辑需要围绕某种类型创建包装器。然而，由于额外的堆内存分配问题，它会引入运行时的性能开销。此外，如果被包装的类型是原生类型，性能的损失是很糟糕的，因为原生类型通常在运行时就进行了大量优化，然而他们的包装器却没有得到任何特殊的处理。

同时，内联类可以声明属性与函数：

``` kotlin
inline class Name(val s: String) {
    val length: Int
        get() = s.length

    fun greet() {
        println("Hello, $s")
    }
}    

fun main() {
    val name = Name("Kotlin")
    name.greet() // `greet` 方法会作为一个静态方法被调用
    println(name.length) // 属性的 get 方法会作为一个静态方法被调用
}
```

然而，内联类的成员也有一些限制：

- 内联类不能含有 *init* 代码块

- 内联类不能含有

  幕后字段

  - 因此，内联类只能含有简单的计算属性（不能含有延迟初始化/委托属性）



### 委托



