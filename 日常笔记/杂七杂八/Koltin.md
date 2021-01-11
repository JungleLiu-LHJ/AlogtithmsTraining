# Kotlin



### 类与继承

#### 构造函数

在 Kotlin 中的一个类可以有一个**主构造函数**以及一个或多个**次构造函数**。主构造函数是类头的一部分：它跟在类名（与可选的类型参数）后。

```kotlin
class Person constructor(firstName: String) { /*……*/ }
```

如果主构造函数没有任何注解或者可见性修饰符，可以省略这个 *constructor* 关键字。

```kotlin
class Person(firstName: String) { /*……*/ }
```

**注意**：主构造函数不能包含任何的代码，

如果构造函数有注解或可见性修饰符，这个 *constructor* 关键字是必需的，并且这些修饰符在它前面：

```kotlin
class Customer public @Inject constructor(name: String) { /*……*/ }
```

`init`：是主构造函数代码的一部分。

 

覆盖（重写）方法和属性：

```kotlin
open class Shape {
    open fun draw() { /*……*/ }
    fun fill() { /*……*/ }
}

class Circle() : Shape() {
    override fun draw() { /*……*/ }
}
```

>  如果函数没有标注 *open* 如 `Shape.fill()`，那么子类中不允许定义相同签名的函数， 不论加不加 **override**。将 *open* 修饰符添加到 final 类（即没有 *open* 的类）的成员上不起作用。

```kotlin
open class Shape {
    open val vertexCount: Int = 0
}

class Rectangle : Shape() {
    override val vertexCount = 4
}
```

>`var`是可写可读，`val`是可读。可以用一个 `var` 属性覆盖一个 `val` 属性，但反之则不行。 这是允许的，因为一个 `val` 属性本质上声明了一个 `get` 方法， 而将其覆盖为 `var` 只是在子类中额外声明一个 `set` 方法。



在一个内部类中访问外部类的超类，可以通过由外部类名限定的 *super* 关键字来实现：`super@Outer`：

```kotlin
class FilledRectangle: Rectangle() {
    fun draw() { /* …… */ }
    val borderColor: String get() = "black"
    
    inner class Filler {
        fun fill() { /* …… */ }
        fun drawAndFill() {
            super@FilledRectangle.draw() // 调用 Rectangle 的 draw() 实现
            fill()
            println("Drawn a filled rectangle with color ${super@FilledRectangle.borderColor}") // 使用 Rectangle 所实现的 borderColor 的 get()
        }
    }
}
```

为了表示采用从哪个超类型继承的实现，我们使用由尖括号中超类型名限定的 *super*，如 `super<Base>`：

```kotlin
open class Rectangle {
    open fun draw() { /* …… */ }
}

interface Polygon {
    fun draw() { /* …… */ } // 接口成员默认就是“open”的
}

class Square() : Rectangle(), Polygon {
    // 编译器要求覆盖 draw()：
    override fun draw() {
        super<Rectangle>.draw() // 调用 Rectangle.draw()
        super<Polygon>.draw() // 调用 Polygon.draw()
    }
}
```





### 属性与字段

Kotlin 类中的属性既可以用关键字 *var* 声明为可变的，也可以用关键字 *val* 声明为只读的。

```kotlin
var <propertyName>[: <PropertyType>] [= <property_initializer>]
    [<getter>]
    [<setter>]
```

