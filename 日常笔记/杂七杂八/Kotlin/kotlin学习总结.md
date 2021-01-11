# Kotlin



## 基础特性

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

- 变量需要手动初始化，所以不初始化会报错

- 变量默认为非空，所以初始化时赋值为null也会报错

- 变量用？号设置为可空，使用的时候又报错

  > 关注的都是使用的时候

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

- 不可控类型
- 使用IntArray、FloatArray等



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

> 如果函数没有标注 *open* 如 `Shape.fill()`，那么子类中不允许定义相同签名的函数， 不论加不加 **override**。将 *open* 修饰符添加到 final 类（即没有 *open* 的类）的成员上不起作用。

```kotlin
open class Shape {
    open val vertexCount: Int = 0
}

class Rectangle : Shape() {
    override val vertexCount = 4
}
```

> `var`是可写可读，`val`是可读。可以用一个 `var` 属性覆盖一个 `val` 属性，但反之则不行。 这是允许的，因为一个 `val` 属性本质上声明了一个 `get` 方法， 而将其覆盖为 `var` 只是在子类中额外声明一个 `set` 方法。



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

### 主构造函数

可以写可以不写

```kotlin
class User constructor(name: String) {
    var name: String
    init {
        this.name = name
    }
}
```

> 其中 `init` 代码块是紧跟在主构造器之后执行的，这是因为主构造器本身没有代码体，`init` 代码块就充当了主构造器代码体的功能。

哪些需要用柱构造函数：

- 必须性：创建类的对象时，不管使用哪个构造器，都需要主构造器的参与
- 第一性：在类的初始化过程中，首先执行的就是主构造器

当有次构造函数的时候：

```kotlin
class User constructor(var name: String) {
                                   // 👇  👇 直接调用主构造器
    constructor(name: String, id: Int) : this(name) {
    }
                                                // 👇 通过上一个次构造器，间接调用主构造器
    constructor(name: String, id: Int, age: Int) : this(name, id) {
    }
}
```



**函数使用`=`直接返回值。**

```kotlin
fun sayHi(name: String) = println("Hi " + name)
```





### 字符串

```kotlin
val name = "world"
println("Hi ${name.length}") 
```



#### 原生字符串（raw string）

有时候我们不希望写过多的转义字符，这种情况 Kotlin 通过「原生字符串」来实现。

用法就是使用一对 `"""` 将字符串括起来：

```kotlin
val name = "world"
val myName = "kotlin"
           👇
val text = """
      Hi $name!
    My name is $myName.\n
"""
println(text)
```

```
      Hi world!
My name is kotlin.\n
```

原生字符串还可以通过 `trimMargin()` 函数去除每行前面的空格：

```kotlin
val text = """
     👇 
      |Hi world!
    |My name is kotlin.
""".trimMargin()
println(text)
```

```
Hi world!
My name is kotlin
```

这里的 `trimMargin()` 函数有以下几个注意点：

- `|` 符号为默认的边界前缀，前面只能有空格，否则不会生效
- 输出时 `|` 符号以及它前面的空格都会被删除
- 边界前缀还可以使用其他字符，比如 `trimMargin("/")`，只不过上方的代码使用的是参数默认值的调用方式







### 条件控制



```kotlin
val max = if (a > b) a else b
```

> 注意：Kotlin弃用了三元运算符（条件？然后：否则）

当然，上面的a、b也可以替换成代码块



```kotlin
when (x) {
    👇
    1, 2 -> print("x == 1 or x == 2")
    else -> print("else")
}
```

对数组的遍历：

```kotlin
val array = intArrayOf(1, 2, 3, 4)
          👇
for (item in array) {
    ...
}
```

这里与 Java 有几处不同：

- 在 Kotlin 中，表示单个元素的 `item` ，不用显式的声明类型
- Kotlin 使用的是 `in` 关键字，表示 `item` 是 `array` 里面的一个元素



#### ？.和？..

```kotlin
var length: Int = str?.length
//                👆 ，IDE 报错，Type mismatch. Required:Int. Found:Int?
val length: Int = str?.length ?: -1  
//        如果左侧表达式str?.length为空，则返回-1
```

```kotlin
fun validate(user: User) {
    val id = user.id ?: return // 👈 验证 user.id 是否为空，为空时 return 
}
```



#### `==`和`===`



- `==` ：可以对基本数据类型以及 `String` 等类型进行内容比较，相当于 Java 中的 `equals`
- `===` ：对引用的内存地址进行比较，相当于 Java 中的 `==`

```kotlin
val str1 = "123"
val str2 = "123"
println(str1 == str2) // 👈 内容相等，输出：true

val str1= "字符串"
val str2 = str1
val str3 = str1
print(str2 === str3) // 👈 引用地址相等，输出：true
```



### `Constructor`

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



### `init`

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

### `final`与`val`

不同：val的`get()`可以更改值。



### `static`与`Object`

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

> 能写在top-level就写在top-level

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

> - 如果想写工具类的功能，直接创建文件，写 top-level「顶层」函数。
> - 如果需要继承别的类或者实现接口，就用 `object` 或 `companion object`。



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

### 

```kotlin
val intArray = intArrayOf(1, 2, 3)
val strList = listOf("a", "b", "c")
```

下面是几种操作函数：

- `forEach`：遍历每一个元素

```kotlin
intArray.forEach { i ->
    print(i + " ")
}
```

- `filter`:过滤器

```kotlin
val newList: List = intArray.filter { i ->
    i != 1 // 👈 过滤掉数组中等于 1 的元素
}
```

- `map`:遍历每个元素并执行给定表达式（像滤波器一样）

```kotlin
val newList: List = intArray.map { i ->
    i + 1 // 👈 每个元素加 1
}
```

- `flatMap`：遍历每个元素，并为每个元素创建新的集合

```kotlin
intArray.flatMap { i ->
    listOf("${i + 1}", "a") // 👈 生成新集合
}
```



#### Range

`val range: IntRange = 0..1000 `: [0,1000]

`val range: IntRange = 0 until 1000 `:[0.1000)

可以设置步长：

```kotlin
val range = 0..1000
//               👇 步长为 2，输出：0, 2, 4, 6, 8, 10,....1000,
for (i in range step 2) {
    print("$i, ")
}
```

递减区间：

```kotlin
for (i in 4 downTo 1) {
    print("$i, ")
}
```



#### Sequence

惰性集合

```kotlin
val sequence = sequenceOf(1, 2, 3, 4)
val result: List = sequence
    .map { i ->
        println("Map $i")
        i * 2 
    }
    .filter { i ->
        println("Filter $i")
        i % 3  == 0 
    }
👇
println(result.first()) // 👈 只取集合的第一个元素
```

- 惰性的概念首先就是说在「👇」标注之前的代码运行时不会立即执行，它只是定义了一个执行流程，只有 `result` 被使用到的时候才会执行

- 当「👇」的 `println` 执行时数据处理流程是这样的：

  - 取出元素 1 -> map 为 2 -> filter 判断 2 是否能被 3 整除
  - 取出元素 2 -> map 为 4 -> filter 判断 4 是否能被 3 整除
  - ...

  惰性指当出现满足条件的第一个元素的时候，`Sequence` 就不会执行后面的元素遍历了，即跳过了 `4` 的遍历

而普通的`list`是没有惰性的，会遍历所有的的元素

`sequence`的实现的优点：

- 一旦满足遍历退出的条件，就可以省略后续不必要的遍历过程。

- 像 `List` 这种实现 `Iterable` 接口的集合类，每调用一次函数就会生成一个新的 `Iterable`，下一个函数再基于新的 `Iterable` 执行，每次函数调用产生的临时 `Iterable` 会导致额外的内存消耗，而 `Sequence` 在整个流程中只有一个。

  

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

```kotlin
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

```kotlin
inline class Password(val value: String)
// 不存在 'Password' 类的真实实例对象
// 在运行时，'securePassword' 仅仅包含 'String'
val securePassword = Password("Don't try this in production")
```

这就是内联类的主要特性，它灵感来源于 “inline” 这个名称：类的数据被 “内联”到该类使用的地方

> 有时候，业务逻辑需要围绕某种类型创建包装器。然而，由于额外的堆内存分配问题，它会引入运行时的性能开销。此外，如果被包装的类型是原生类型，性能的损失是很糟糕的，因为原生类型通常在运行时就进行了大量优化，然而他们的包装器却没有得到任何特殊的处理。

同时，内联类可以声明属性与函数：

```kotlin
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





## 协程



### 协程是什么

一套由Kotlin官方提供的API

协程就是切线程，挂起就是可以自动切回来的切线程。



### 基本使用

```kotlin
// 方法一，使用 runBlocking 顶层函数
runBlocking {
    getImage(imageId)
}

// 方法二，使用 GlobalScope 单例对象
//            👇 可以直接调用 launch 开启协程
GlobalScope.launch {
    getImage(imageId)
}

// 方法三，自行通过 CoroutineContext 创建一个 CoroutineScope 对象
//                                    👇 需要一个类型为 CoroutineContext 的参数
val coroutineScope = CoroutineScope(context)
coroutineScope.launch {
    getImage(imageId)
}
```

- 方法一通常适用于单元测试的场景，而业务开发中不会用到这种方法，因为它是线程阻塞的。
- 方法二和使用 `runBlocking` 的区别在于不会阻塞线程。但在 Android 开发中同样不推荐这种用法，因为它的生命周期会和 app 一致，且不能取消（什么是协程的取消后面的文章会讲）。
- 方法三是比较推荐的使用方法，我们可以通过 `context` 参数去管理和控制协程的生命周期（这里的 `context` 和 Android 里的不是一个东西，是一个更通用的概念，会有一个 Android 平台的封装来配合使用）

上面的都是不常用，常用的如下：

```kotlin
coroutineScope.launch(Dispatchers.IO) {
    ...
}
```



### 协程怎么用

- 项目根目录下的 `build.gradle` :

```kotlin
buildscript {
    ...
    // 👇
    ext.kotlin_coroutines = '1.3.1'
    ...
}
```

- Module 下的 `build.gradle` :

```kotlin
dependencies {
    ...
    //                                       👇 依赖协程核心库
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines"
    //                                       👇 依赖当前平台所对应的平台库
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlin_coroutines"
    ...
}
```

这种写法看上去好像和刚才那种区别不大，但如果你需要频繁地进行线程切换，这种写法的优势就会体现出来。可以参考下面的对比：

```kotlin
coroutineScope.launch(Dispatchers.Main) {      // 👈 在 UI 线程开始
    val image = withContext(Dispatchers.IO) {  // 👈 切换到 IO 线程，并在执行完成后切回 UI 线程
        getImage(imageId)                      // 👈 将会运行在 IO 线程
    }
    avatarIv.setImageBitmap(image)             // 👈 回到 UI 线程更新 UI
}
```



### suspend

suspend :挂起函数，执行到的时候会挂起，不阻碍当前线程。

Suspend的作用：提醒，提醒这是一个耗时函数，需要在协程里调用

需要`suspend`的时候：

耗时操作一般分为两类：I/O 操作和 CPU 计算工作。比如文件的读写、网络交互、图片的模糊处理，都是耗时的，通通可以把它们写进 `suspend` 函数里。



### 挂起

挂起：稍后会被切回来的线程切换，是在协程里面的特权，所以必须在协程里面被调用

当该线程执行到suspend之后，兵分两路，一边是之前的线程，一边是协程：

**线程**：

跳出协程块。

如果它是一个后台线程：

- 要么无事可做，被系统回收
- 要么继续执行别的后台任务

跟 Java 线程池里的线程在工作结束之后是完全一样的：回收或者再利用。

如果这个线程它是 Android 的主线程，那它接下来就会继续回去工作：也就是一秒钟 60 次的界面刷新任务。



**协程**：

协程会从在指定线程从`suspend`函数开始继续往下执行，指定线程是函数内部的 `withContext` 传入的 `Dispatchers.IO` 所指定的 IO 线程。

常用的 `Dispatchers` ，有以下三种：

- `Dispatchers.Main`：Android 中的主线程
- `Dispatchers.IO`：针对磁盘和网络 IO 进行了优化，适合 IO 密集型的任务，比如：读写文件，操作数据库以及网络请求
- `Dispatchers.Default`：适合 CPU 密集型的任务，比如计算

​             

之后会切回来，当这个函数执行完毕后，线程又切了回来，「切回来」也就是协程会帮我再 `post` 一个 `Runnable`，让我剩下的代码继续回到主线程去执行。



### 非阻塞式挂起：

不卡线程，其实Java的Thread和线程池也是非阻塞式的，

Kotlin的不同就是看起来阻塞的代码写了非阻塞的操作



说到这里，Kotlin 协程的三大疑问：协程是什么、挂起是什么、挂起的非阻塞式是怎么回事，就已经全部讲完了。非常简单：

- 协程就是切线程；
- 挂起就是可以自动切回来的切线程；
- 挂起的非阻塞式指的是它能用看起来阻塞的代码写出非阻塞的操作，就这么简单。