# Kotlin 的 Lamda



## kotlin 的高阶函数

所谓高阶函数，就是参数有函数类型或者返回值是函数类型的函数，就叫高阶函数。

```kotlin
fun a(funParam: (Int) -> String): String {
  return funParam(1)
}
```

```kotlin
fun b(param: Int): String {
  return param.toString()
}
a(b)
```

除了作为函数的参数和返回值类型，把它赋值给一个变量也是可以的。

```kotlin
a(::b)
val d = ::b
```



## 双冒号 ::method 是什么

简而言之，::method就是这个和method函数有相同功能的**对象**。

>Kotlin 里「函数可以作为参数」这件事的本质，是函数在 Kotlin 里可以作为对象存在——因为只有对象才能被作为参数传递啊。赋值也是一样道理，只有对象才能被赋值给变量啊。但 Kotlin 的函数本身的性质又决定了它没办法被当做一个对象。那怎么办呢？Kotlin 的选择是，那就创建一个和函数具有相同功能的对象。怎么创建？使用双冒号。
>
>在 Kotlin 里，一个函数名的左边加上双冒号，它就不表示这个函数本身了，而表示一个对象，或者说一个指向对象的引用，但，这个对象可不是函数本身，而是一个和这个函数具有相同功能的对象。

```kotlin
b(1) // 调用函数
d(1) // 用对象 a 后面加上括号来实现 b() 的等价操作
(::b)(1) // 用对象 :b 后面加上括号来实现 b() 的等价操作
```

函数类型的对象可以加括号来调用。为什么？因为这其实是个假的调用，它是 Kotlin 的语法糖，实际上你对一个函数类型的对象加括号、加参数，它真正调用的是这个对象的 invoke() 函数：

```kotlin
d(1) // 实际上会调用 d.invoke(1)
(::b)(1) // 实际上会调用 (::b).invoke(1)
```

> 双冒号加上函数名的这个写法，它是一个指向对象的引用，但并不是指向函数本身，而是指向一个我们在代码里看不见的对象。这个对象复制了原函数的功能，但它并不是原函数。



## 匿名函数

```kotlin
a(fun b(param: Int): String {
  return param.toString()
});
val d = fun b(param: Int): String {
  return param.toString()
}
//kotlin其实不允许这么写
```

这种写法的话，函数的名字其实就没用了，所以你可以把它省掉：

```kotlin
a(fun(param: Int): String {
  return param.toString()
});
val d = fun(param: Int): String {
  return param.toString()
}
```

Kotlin的回调也可以写成：

```kotlin
fun setOnClickListener(onClick: (View) -> Unit) {
  this.onClick = onClick
}
view.setOnClickListener(fun(v: View): Unit) {
  switchToNextPage()
})
```

写成Lambda表达式是：

```kotlin
view.setOnClickListener({ v: View ->
  switchToNextPage()
})
```



## Lambda表达式

```kotlin
view.setOnClickListener() { v: View ->
  switchToNextPage()
}
```

如果 Lambda 是函数唯一的参数，你还可以直接把括号去了

```kotlin
view.setOnClickListener { v: View ->
  switchToNextPage()
}
```

```kotlin
view.setOnClickListener {
  switchToNextPage()
}
```

需要用的话用it来代替

```kotlin
view.setOnClickListener {
  switchToNextPage()
  it.setVisibility(GONE)
}
```

Lambda 的返回值别写 return，如果你写了，它会把这个作为它外层的函数的返回值来直接结束外层函数。当然如果你就是想这么做那没问题啊，但如果你是只是想返回 Lambda，这么写就出错了



## Kotlin里匿名函数和Lambda表达式

 Kotlin 的匿名函数**不是函数**。它是个对象。匿名函数虽然名字里有「函数」两个字，包括英文的原名也是 Anonymous Function，但它其实不是函数，而是一个对象，一个函数类型的对象。它和双冒号加函数名是一类东西，和函数不是。

同理，Lambda 其实也是一个函数类型的对象而已。你能怎么使用双冒号加函数名，就能怎么使用匿名函数，以及怎么使用 Lambda 表达式。

 Kotlin 的匿名函数和 Lambda 表达式的本质，它们都是函数类型的对象。