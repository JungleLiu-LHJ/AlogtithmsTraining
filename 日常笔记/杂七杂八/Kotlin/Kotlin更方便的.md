# Kotlin 3

## 主构造函数

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





## 字符串

```kotlin
val name = "world"
println("Hi ${name.length}") 
```



### 原生字符串（raw string）

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



## 数组和集合

```kotlin
val intArray = intArrayOf(1, 2, 3)
val strList = listOf("a", "b", "c")
```

下面是几种操作函数：

* `forEach`：遍历每一个元素

```kotlin
intArray.forEach { i ->
    print(i + " ")
}
```

* `filter`:过滤器

```kotlin
val newList: List = intArray.filter { i ->
    i != 1 // 👈 过滤掉数组中等于 1 的元素
}
```

* `map`:遍历每个元素并执行给定表达式（像滤波器一样）

```kotlin
val newList: List = intArray.map { i ->
    i + 1 // 👈 每个元素加 1
}
```

* `flatMap`：遍历每个元素，并为每个元素创建新的集合

```kotlin
intArray.flatMap { i ->
    listOf("${i + 1}", "a") // 👈 生成新集合
}
```



### Range



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



### Sequence

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



## 条件控制



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



### ？.和？..

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



### `==`和`===`



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





局部函数（local function）



