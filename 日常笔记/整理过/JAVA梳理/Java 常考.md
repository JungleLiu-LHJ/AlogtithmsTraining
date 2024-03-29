# Java 总结

## 集合

### List

![img](.\List)



#### ArrayList、Vector、LinkedList

1. ArrayList和Vector底层是用数组实现的，Vector是线程同步的。LinkedList是通过双链表实现的
2. **扩容**：ArrayList被第一次创建的时候，会有一个初始大小，随着不断向ArrayList中增加元素，当ArrayList认为容量不够的时候就会进行扩容。Vector缺省情况下自动增长原来一倍的数组长度，ArrayList增长原来的50%
3. **效率**:  增加：LinedList快   删除：LinedList快   查询：ArrayList快   改(赋值)：ArrayList快 。Vector因为有锁会比ArrayList慢一点![img](.\链表性能)





### Map

![img](.\map.png)

- Collection中的集合，元素是孤立存在的（理解为单身），向集合中存储元素采用一个个元素的方式存储。
- Map中的集合，元素是成对存在的(理解为夫妻)。每个元素由键与值两部分组成，通过键可以找对所对应的值。 
- Collection中的集合称为单列集合，Map中的集合称为双列集合。
- 需要注意的是，Map中的集合不能包含重复的键，值可以重复；每个键只能对应一个值。

#### HashMap

![image-20201104002454234 (2)](.\image-20201104002454234 (2).png)

结构：数组+链表+红黑树的结构。哈希桶采用数组，链表数据大于8的时候改成红黑树，当长度降到6时转成链表。当数组长度小于64的时候不会转成红黑树，数组大于64的时候才会转成红黑树

##### 红黑树（java8中）

> * BST（二叉搜索树)：左边的所有节点比根节点小，右边所有节点比根节点大。查询：O(logn）,最坏O(n）（递增的时候）
> * AVL(自平衡二叉树)：左右深度差≤1的BST，读写复杂度是O(logn)（最坏也是）
>
> 因为相对于AVL平衡左右深度条件宽松很多（可以差1倍），所以插入的时候可以减少很多操作。但查询复杂度仍然是O(logn)

hashMap的长度n必须为2^k，因为计算位置的时候需要n-1 = 11...11，如下图所示：

![image-20201104002903666](.\image-20201104002903666.png)

> 其中hashCode是该对象的hash值（每个对象有一个hash值）

#### LinkedHashMap

有序的hashMap，继承自hashMap。其实就是**hashMap + 双向链表**，把每个新增的Entry放到双向链表里面。Android中的LruCache用到了LinkedHashMap，也是线程不安全的。



#### TreeMap

内部会对key进行排序（key需要重写`Comparable`）。内部是红黑树结构



#### HashTable

继承自Dictionary。

**线程安全的**，每个方法都加了synchronized。

不可以传空值（HashMap可以传），hash值计算方式和下标（index）计算方式都不一样，因为HashTable的：

![image-20210225232222310](.\image-20210225232222310.png)

HashMap的：

![image-20210225232302768](.\image-20210225232302768.png)

Hashtable的扩容和hashMap不同，



#### ConcurrentHashMap

##### JDK 1.7

![image-20210228184950972](.\image-20210228184950972.png)

分段锁。每一个Segment有一把锁，其中每个Segment的Entry数量是一样的，每个Segment就像是一个Hashmap，且Segment是继承 ReentrantLock的

添加：通过hash计算放在第几个Segment （通过UNSAFE同步） --->计算在Segment里面的的Index  (不断处理逻辑并且tryLock() )

> Unsafe类相当于是一个java语言中的后门类，**提供了硬件级别的原子操作**，所以在一些并发编程中被大量使用

##### JDK1.8以后

> 1.8中：new的时候传进去的初始大小为initialCapacity，经过tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1))

```java
public ConcurrentHashMap(int initialCapacity,
                         float loadFactor, int concurrencyLevel) {
    if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
        throw new IllegalArgumentException();
    if (initialCapacity < concurrencyLevel)   // Use at least as many bins
        initialCapacity = concurrencyLevel;   // as estimated threads
    long size = (long)(1.0 + (long)initialCapacity / loadFactor);
    int cap = (size >= (long)MAXIMUM_CAPACITY) ?
        MAXIMUM_CAPACITY : tableSizeFor((int)size);
    this.sizeCtl = cap;
}
```



添加元素： 

```java
计算hash
for (Node<K,V>[] tab = table;;) {  //table是hash的数组，成员变量

  	if(tab == null) 
  	 	通过 cas+自旋 初始化数组, sizeCtl: 初始容量->-1 ->扩容阈值
 	 else if(f = tabAt(tab, i = (n - 1) & hash)) == null) 
  	 	cas+自旋（和外侧的for构成自旋循环）来添加Node
 	 else if(正在扩容(hash == -1)) 
  		帮助扩容
  	 else {
  		synchronized (f) {
  		 if (fh >= 0)  //代表普通的链表结构
  	 		则循环遍历链表：有则覆盖，没有则在尾部添加
  		 else if(f is 树结构)
  	 		添加到红黑树里面
  		}
 	 }
  	判断是否需要转成树
	维护集合长度，并判断是否要进行扩容操作
}
```

**注意：以上这些构造方法中，都涉及到一个变量`sizeCtl`，这个变量是一个非常重要的变量，而且具有非常丰富的含义，它的值不同，对应的含义也不一样，这里我们先对这个变量不同的值的含义做一下说明，后续源码分析过程中，进一步解释**

`sizeCtl`为0，代表数组未初始化， 且数组的初始容量为16

`sizeCtl`为正数，如果数组未初始化，那么其记录的是数组的初始容量，如果数组已经初始化，那么其记录的是数组的扩容阈值

`sizeCtl`为-1，表示数组正在进行初始化

`sizeCtl`小于0，并且不是-1，表示数组正在扩容， -(1+n)，表示此时有n个线程正在共同完成数组的扩容操作





### Set



#### HashSet



## 线程



### 创建

1. Thread和Runnable,两个其实底层是一样的，都调用的Thread.run()方法
2. ThreadFactory,一个创建Thread的工厂方法
3. 线程池和Executor

线程池：

```java
  /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *        pool
     * @param keepAliveTime when the number of threads is greater than
     *        the core, this is the maximum time that excess idle threads
     *        will wait for new tasks before terminating.
     * @param unit the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for holding tasks before they are
     *        executed.  This queue will hold only the {@code Runnable}
     *        tasks submitted by the {@code execute} method.
     * @param threadFactory the factory to use when the executor
     *        creates a new thread
     * @param handler the handler to use when execution is blocked
     *        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue}
     *         or {@code threadFactory} or {@code handler} is null
     */
public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) 
```

>corePoolSize:线程池中线程的默认数，创建线程池的时候就有几个线程。等创建的线程数超过这个数的时候**多出来**的会被回收
>
>maximumPoolSize:线程的上限，多出来的会等待
>
>keepAliveTime: 时间，等待被回收的时间
>
>unit:数字的单位
>
>workQueue:
>
>threadFactory:
>
>handler:
>
>

4. Callable和future

```java
Callable<String> callable = new Callable<String>() {
 @Override
 public String call() {
 try {
 Thread.sleep(1500);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }
 return "Done!";
 }
};
ExecutorService executor =
Executors.newCachedThreadPool();
Future<String> future = executor.submit(callable);
try {
    if(future.isDone()) String result = future.get(); //可以一直循环判断
 System.out.println("result: " + result);
} catch (InterruptedException | ExecutionException e)
{
 e.printStackTrace();
}
```







### 线程状态

![ ](.\ThreadState.jpg)

为什么Thread.sleep需要try..catch，因为sleep的时候如果外部调用了该线程的interrupt（为了不浪费资源结束线程）会通过让sleep抛出异常来中断sleep。注意：1. 当睡眠中外部调了interrupte线程里面的isInterrupt一直是false   2.Thread.isInterrupt()调用之后会重新置位false,会改状态



### 锁/同步

> 乐观并发控制：不太会出现冲突的时候就用乐观并发控制，比如，先不加锁，读的时候不加锁，写的时候比较一下和之前一样不一样，不一样就撤回。就是相信并发情况比较少，就先不加锁，等发生的时候多花点成本去撤回数据就行
>
> 悲观锁：相信经常发生并发场景，读的时候就上锁，其他线程不能读  



#### volatile（无法保证原子性）

当使用violate去申明一个变量时，就等于告诉了虚拟机这个变量极有可能会被某些程序或者线程修改。为了确保这个变量被修改后，应用程序中的其他的线程都可以看到这个改动，虚拟机就必须采用一些特殊的手段，保证这个变量的可见性等特点。

![img](https://img-blog.csdn.net/20170311100744354?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvaXRfZHg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

比如两个线程进行x++的操作的时候，会出现问题。因为x++不是原子性操作，x++ : temp = x +1; x = temp

``` java
x++ 相当于：
temp = x +1;  //1
x = temp;     //2    
```

假设x=2,线程A,B同时执行++,此时就可能出现，线程A执行了1，temp = 2+1，此时发生了线程切换，然后B线程中，temp = 2+1，x = temp = 3。然后又切换到A线程,x=3。这样就出现了同步问题。

>关于双重锁单例模式为什么要用violate
>
>```java
>class Singleton{
>
>    private volatile static Singleton singleton;
>
>    private Singleton(){}       
>    public static Singleton getInstance(){       
>        if(singleton == null){                  // 1
>            synchronized(Singleton.class){      // 2
>                if(singleton == null){          // 3
>                    singleton = new Singleton(); // 4
>                }
>            }
>        } 
>        return singleton;           
>    }
>
>}
>
>```
>
>关于3：线程A和线程B都判断了1了，进入2，线程A先进入临界区，线程B发现线程A进入了临界区，就挂在了`Singleton.class`等等待队列中，等待线程A执行完成。线程A继续执行，创建了一个`singleton`实例。退出了临界区。然后线程B被唤醒，进入临界区，又创建了一个`singleton`实例。结果又创建了两个`singleton`实例。
>
>这里4为什么需要用violate？
>
>要理解为什么要加volatile，首先要理解`new Singleton()`做了什么。new一个对象有几个步骤。1.看class对象是否加载，如果没有就先加载class对象，2.分配内存空间，初始化实例，3.调用构造函数，4.返回地址给引用。而cpu为了优化程序，可能会进行指令重排序，打乱这3，4这几个步骤，导致实例内存还没分配，就被使用了。
>
>再用线程A和线程B举例。线程A执行到`new Singleton()`，开始初始化实例对象，由于存在指令重排序，这次new操作，先把引用赋值了，还没有执行构造函数。这时时间片结束了，切换到线程B执行，线程B调用`new Singleton()`方法，发现引用不等于`null`，就直接返回引用地址了，然后线程B执行了一些操作，就可能导致线程B使用了还没有被初始化的变量。
>
>

#### Synchronize的原理（偏向锁到重量级锁）

使用：

* 方法上用synchronized，则取得的锁是该对象

* synchronized作用的对象是静态方法或一个类，则它取得的锁是类，类是全局唯一的，所以任何都可以锁住

* 用一个object来进行synchronized，则是该对象来进行锁

  

JAVA对象：对象头、实例数据（属性、方法）、填充对齐字节（为了满足Java对象的大小必须是8bit的倍数）

对象头：1.Mark Word   2. Class Point  (指向了当前对象类型所在方法区的类型数据)

Mark Word:

![image-20210309234526927](.\image-20210309234526927.png)



> synchronized  被编译后的字节码中会生成 ` monitorenter` ....  ` monitorexit` 指令
>
> Monitor(类似监管器)是依赖操作系统的mutex lock来实现的，需要切换操作系统的内核态，用户态操作内核态是比较耗时的。

​    无锁 --->偏向锁--->轻量级锁--->重量级锁       只能升级不能降级

* **无锁**：所有线程都能访问，可以通过CAS方式来实现同步

* **偏向锁**：通过mark Word中的前23个bit保存的线程id来确定是不是同一个线程在操作，如果多个锁在竞争则会升级到轻量级锁

  >
  >
  >- （1）访问Mark Word中偏向锁的标识是否设置成1，锁标志位是否为01——确认为可偏向状态。
  >- （2）如果为可偏向状态，则测试线程ID是否指向当前线程，如果是，进入步骤（5），否则进入步骤（3）。
  >- （3）如果线程ID并未指向当前线程，则通过CAS操作竞争锁。如果竞争成功，则将Mark Word中线程ID设置为当前线程ID，然后执行（5）；如果竞争失败，执行（4）。
  >- （4）如果CAS获取偏向锁失败，则表示有竞争（CAS获取偏向锁失败说明至少有过其他线程曾经获得过偏向锁，因为线程不会主动去释放偏向锁）。当到达全局安全点（safepoint）时，会首先暂停拥有偏向锁的线程，然后检查持有偏向锁的线程是否活着（因为可能持有偏向锁的线程已经执行完毕，但是该线程并不会主动去释放偏向锁），如果线程不处于活动状态，则将对象头设置成无锁状态（标志位为“01”），然后重新偏向新的线程；如果线程仍然活着，撤销偏向锁后升级到轻量级锁状态（标志位为“00”），此时轻量级锁由原持有偏向锁的线程持有，继续执行其同步代码，而正在竞争的线程会进入自旋等待获得该轻量级锁。
  >- （5）执行同步代码。
  >
  >偏向锁的释放：偏向锁使用了一种等到竞争出现才释放偏向锁的机制：偏向锁只有遇到其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁，线程不会主动去释放偏向锁。

* **轻量级锁**：当一个线程进入的时候    

  1. 通过cas来获取锁，如果获取到则执行2，没有则自旋
  2. 会在该线程的虚拟机栈中Lock Record中保存该对象的Mark Word的副本和owner指针
  3. 对象的mark Word的前30bit会生成一个指针指向线程的Lock Record
  4. 如果自旋等待的线程超过1个，轻量级锁会升级到重量级锁

> 自旋不用进行系统中断和现场恢复，所以其效率跟高。CPU差不多是CPU在空转

* **重量级锁**：通过Monitor来进行管控

![image-20210310001316927](.\image-20210310001316927.png)



#### 谈谈CAS

互斥锁（悲观锁）：将资源锁定只供一个线程调用，也叫悲观锁

但是有些时候大部分调用是读操作，或者同步代码块执行的耗时远远小于线程切换的耗时的时候，就不想用悲观锁。就想用CAS（Compare and Swap）

各种不同类型的CPU都提供了CAS的支持。通过调用底层的CAS就可以实现原子化操作。

java中`AtomicInteger`等就是通过cas来进行同步的（也叫乐观锁）



#### AQS (AbstractQueuedSynchronizer)

用一个双向FIFO队列来保存等待的线程。

1. head节点是拿到锁正在运行的线程
2. head->next的节点则是在自旋的等待的线程
3. 后面的线程是挂起中



其中几个比较重要的点：

* 唤醒的时候执行了release，然后会从尾结点开始搜索，找到最靠前的waitStatus<=0的节点。

> 为什么从后往前搜索？ 因为tryAcquire的时候，node与前一个节点t之间，node的prev指针在cas操作之前已经建立，而t的next指针还未建立。此时若其他线程调用了unpark操作，从头开始找就无法遍历完整的队列，而从后往前找就可以。
>
> ``` java
>    private Node addWaiter(Node mode) {
>         Node node = new Node(Thread.currentThread(), mode);
>         // Try the fast path of enq; backup to full enq on failure
>         Node pred = tail;
>         if (pred != null) {
>             node.prev = pred;     
>             if (compareAndSetTail(pred, node)) {  //原子操作
>                 pred.next = node;    //非原子操作
>                 return node;
>             }
>         }
>         enq(node);
>         return node;
>     }
> ```

* 当线程处于等待队列中时无法响应外部的中断请求，只有当线程拿到锁之后再进行中断响应

> ![image-20210317232358771](.\image-20210317232358771.png)



![acquire实现流程图](http://assets.processon.com/chart_image/602f37927d9c081db9a6d12c.png)

#### Reentrantlock

![image-20210320161449608](.\image-20210320161449608.png)

从lock中可以看到公平锁和非公平锁的区别：

``` java
   static final class NonfairSync extends Sync {

        /**
         * Performs lock.  Try immediate barge, backing up to normal
         * acquire on failure.
         */
        final void lock() {
            if (compareAndSetState(0, 1)) //这里如果state==0就直接插队获取锁
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);  //乖乖排队
        }

        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }
```



```java
static final class FairSync extends Sync {
    private static final long serialVersionUID = -3000897897090466540L;

    final void lock() {
        acquire(1); 
    }

    /**
     * Fair version of tryAcquire.  Don't grant access unless
     * recursive call or no waiters or is first.
     */
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (!hasQueuedPredecessors() && //没有排在前面的线程，则尝试获取锁
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) { //已经获取了锁。则累加
            int nextc = c + acquires; 
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
}
```



Reetranlock中的`lockInterruptibly()`和`lock()`的区别: `lock()`就是`acuqire()`的调用，如果排队等待中调用了中断该线程不会立即抛出异常，而是存储中断的状态值，拿到锁再抛出。 `lockInterruptibly(）` 则是排队过程中如果被调用了interrupt则会放弃排队直接抛出异常。

> java中断机制
>
> ![image-20210320145722634](.\image-20210320145722634.png)

Condition:

![image-20210321132500402](.\image-20210321132500402.png)



#### CountDownLatch

用到AQS的共享模式来实现的,一个线程唤醒，state就+1,被挂起就-1，等于0的时候就让主任务继续执行

![image-20210323220937108](.\image-20210323220937108.png)



![image-20210323221203754](.\image-20210323221203754.png)





### ThreadLock

这个类提供线程局部变量。这些变量与正常的变量不同，每个线程访问一个(通过它的get或set方法)都有它自己的、独立初始化的变量副本。ThreadLocal实例通常是类中的私有静态字段，希望将状态与线程关联(例如，用户ID或事务ID)。

ThreadLock里面的set,get等都是通过操作当前线程的ThreadLocalMap

每个线程内部有个ThreadLocalMap，ThreadLocalMap里面保存着若干

Entry键值对：

```java
static class Entry extends WeakReference<ThreadLocal<?>> {
    /** The value associated with this ThreadLocal. */
    Object value;

    Entry(ThreadLocal<?> k, Object v) {
        super(k);
        value = v;
    }
}
```

ThreadLocalMap的set方法:

```java
1  private void set(ThreadLocal<?> key, Object value) {
 8             Entry[] tab = table;
 9             int len = tab.length;
10             int i = key.threadLocalHashCode & (len-1);// 根据哈希码和数组长度求元素放置的位置，即数组下标
11             //从i开始往后一直遍历到数组最后一个Entry
12             for (Entry e = tab[i];
13                  e != null;
14                  e = tab[i = nextIndex(i, len)]) {
15                 ThreadLocal<?> k = e.get();
16                 //如果key相等，覆盖value
17                 if (k == key) {
18                     e.value = value;
19                     return;
20                 }
21                 //如果key为null,用新key、value覆盖，同时清理历史key=null的陈旧数据
22                 if (k == null) {
23                     replaceStaleEntry(key, value, i);
24                     return;
25                 }
26             }
    			//如果key不相等且不为null（没找到同一种类型的ThreadLocal）,则在最后添加一个新的，如下:
28             tab[i] = new Entry(key, value);
29             int sz = ++size;          //如果超过阀值，就需要再哈希了
30             if (!cleanSomeSlots(i, sz) && sz >= threshold)
31                 rehash();
32 }
```









## JVM

![image-20210413235303610](.\image-20210413235303610.png)







### 内存

![preview](https://pic4.zhimg.com/v2-abefb713de46f1e6dd241246c0afe263_r.jpg)

- 堆（Heap）：线程共享。所有的对象实例以及数组都要在堆上分配。回收器主要管理的对象。
- 方法区（Method Area）：线程共享。存储类信息、常量、静态变量、即时编译器编译后的代码。
- 方法栈（JVM Stack）：线程私有。存储局部变量表、操作栈、动态链接、方法出口，对象指针。
- 本地方法栈（Native Method Stack）：线程私有。为虚拟机使用到的Native 方法服务。如Java使用c或者c++编写的接口服务时，代码在此区运行。
- 程序计数器（Program Counter Register）：线程私有。有些文章也翻译成PC寄存器（PC Register），同一个东西。它可以看作是当前线程所执行的字节码的行号指示器。指向下一条要执行的指令。



### GC

#### 垃圾回收

将已分配出去但不再使用的内存收回来，以便再次分配。

判断一个对象是否可以回收有两种方法：

1. 引用计数法： 可以通过引用计数法（reference counting），为每个对象添加一个引用计数器，用来统计指向该对象的引用个数。 一旦某个对象的引用计数器为0，则说明该对象已经死亡，便可以被回收了。**但循环引用的时候会有问题，所以一般不再使用**。
2. 可达性分析法：通过一系列名为**GC Roots**的对象作为起始点，从这些节点开始向下搜索，搜索所走过的路径称为引用链(Reference Chain)，当一个对象到GC Roots没有任何引用链相连时，则证明此对象是不可用的

可达性分析算法的GC Roots:

1. 虚拟机栈（栈帧中的本地变量表）中的引用的对象：java方法栈中的局部变量
2. 方法区中的类静态属性引用的对象：已加载的类中的静态变量
3. 方法区中的常量引用的对象：已启动未停止的java线程
4. 本地方法栈中JNI的引用的对象：JNI



年轻代：Eden区，Survicor区（有S1和S0区），Young GC 的过程是用复制算法

老年代：满了15岁的时候放到Old区，或者大对象直接放到Old区。Old GC会伴随着Young GC。主要用标记-清理算法

1. 把不需要删除的从E区复制到S0区
2. E+S0幸存下来的对象复制到S2区，如此往复
3. 上面每次Young GC每个对象年龄会+1，直到满了15岁就直接到old 区中

新的JDK用G1垃圾收集器



#### 四种引用类型

1. 强引用：正常的引用
2. 软引用：JVM认为内存不足的时候会去清理
3. 弱引用：GC的时候会做清理
4. 虚引用：就像没有引用一样，无法通过虚引用访问对象的属性和函数，一般用来监测什么时候回收之类的

> 作为安卓开发，软引用一般用来处理图片这种大的对象。
>
> 为了避开OOM，可以用软引用，对性能更在意用弱引用





### 类加载过程

 

![preview](https://pic4.zhimg.com/v2-ecf6c3d0f5146029e9693d6223d23afb_r.jpg)



* 加载：把class字节码文件从各个来源通过类加载器装载都内存中
  * 字节码：本地路径下编译生成的.class文件
  * 类加载器：就是classLoader
* 验证：保证加载进来的字节流符合虚拟机规范。包括文件格式、元数据验证、字节码验证和符号引用验证
* 准备：为类变量分配内存，为静态变量赋0值
* 解析：将常量池内的符号引用替換成直接引用的过程
* 初始化：对类变量初始化，对static修饰的变量或语句进行初始化。如果初始化一个类的时候，其父类尚未初始化，则优先初始化其父类。如果同时包含多个静态变量和静态代码块，则按照自上而下的顺序依次执行

> 注意这里解析不一定在初始化之前，比如多态等的时候会后执行解析

