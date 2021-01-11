## 自定义View

#### 自定义View的总调度方法：

> draw() 是绘制过程的总调度方法。一个 View 的整个绘制过程都发生在 `draw()` 方法里。前面讲到的背景、主体、子 View 、滑动相关以及前景的绘制，它们其实都是在 `draw()` 方法里的。

```java
// View.java 的 draw() 方法的简化版大致结构（是大致结构，不是源码哦）：

public void draw(Canvas canvas) {
    ...
    
    drawBackground(Canvas); // 绘制背景（不能重写）
    onDraw(Canvas); // 绘制主体
    dispatchDraw(Canvas); // 绘制子 View
    onDrawForeground(Canvas); // 绘制滑动相关和前景
    
    ...
}
```



#### View生命周期

```java
[改变可见性] --> 构造View() --> onFinishInflate() --> onAttachedToWindow() --> onMeasure() --> onSizeChanged() --> onLayout() --> onDraw() --> onDetackedFromWindow()
```



1. `View`默认为可见的，不是默认值时先调用`onVisibilityChanged`()，但是此时该View的尺寸、位置等信息都不知道。

2. 可见性改变后才是调用带有两个参数的构造函数，当然，如果该View不是在layout中定义的话，会调用一个参数的构造函数。

3. 从`XMl`文件中`inflate`完成（`onFinishInflate`()）。

4. 将`View`加到`window`中（View是gone的，那么View创建生命周期也就结束）。

5. 测量`view`的长宽（`onMeasure`()）。

6. 定位`View` 在父`View`中的位置（`onLayout`()），若`View`是`invisible`，则`View`的创建生命周期结束。

7. 绘制`View`的content（`onDraw`()），只有可见的`View`才在`window`中绘制。

8. `View`的销毁流程和可见性没有关系



#### View.post

> View显示到界面上需要经历onMeasure、onLayout和onDraw三个过程，而View的宽高是在onLayout阶段才能最终确定的，而在Activity#onCreate中并不能保证View已经执行到了onLayout方法，也就是说Activity的声明周期与View的绘制流程并不是一一绑定。那为什么调用post方法就能起作用呢？首先MessageQueue是按顺序处理消息的，而在setContentView()后队列中会包含一条询问是否完成布局的消息，而我们的任务通过View#post方法被添加到队列尾部，保证了在layout结束以后才执行。





## Volatile

作用：强制每次都直接读内存，组织重排序，确保`voltile`类型的值一旦被写入缓必定会被立即更新到主存。  像一个轻锁![img](file:///C:\Users\80264247\AppData\Local\Temp\SGPicFaceTpBq\25860\6778726B.png)







