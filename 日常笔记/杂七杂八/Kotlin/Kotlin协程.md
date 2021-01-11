# Kotlin 协程

## 协程是什么

一套由Kotlin官方提供的API

协程就是切线程，挂起就是可以自动切回来的切线程。

## 基本使用

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



## 协程怎么用

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



## suspend

suspend :挂起函数，执行到的时候会挂起，不阻碍当前线程。

Suspend的作用：提醒，提醒这是一个耗时函数，需要在协程里调用

需要`suspend`的时候：

耗时操作一般分为两类：I/O 操作和 CPU 计算工作。比如文件的读写、网络交互、图片的模糊处理，都是耗时的，通通可以把它们写进 `suspend` 函数里。



## 挂起

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



## 非阻塞式挂起：

不卡线程，其实Java的Thread和线程池也是非阻塞式的，

Kotlin的不同就是看起来阻塞的代码写了非阻塞的操作



说到这里，Kotlin 协程的三大疑问：协程是什么、挂起是什么、挂起的非阻塞式是怎么回事，就已经全部讲完了。非常简单：

- 协程就是切线程；
- 挂起就是可以自动切回来的切线程；
- 挂起的非阻塞式指的是它能用看起来阻塞的代码写出非阻塞的操作，就这么简单。