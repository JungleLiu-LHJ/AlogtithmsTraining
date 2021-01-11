# Java 常考

## 集合

### List

![img](.\List)



#### ArrayList、Vector、LinkedList

1. ArrayList和Vector底层是用数组实现的，Vector是线程同步的。LinkedList是通过双链表实现的
2. **扩容**：ArrayList被第一次创建的时候，会有一个初始大小，随着不断向ArrayList中增加元素，当ArrayList认为容量不够的时候就会进行扩容。Vector缺省情况下自动增长原来一倍的数组长度，ArrayList增长原来的50%
3. **效率**:  增加：LinedList快   删除：LinedList快   查询：ArrayList快   改(赋值)：ArrayList快 。Vector因为有锁会比ArrayList慢一点![img](.\链表性能)





### Map

![img](C:\Users\40515\Desktop\面试准备\JAVA梳理\map)

- Collection中的集合，元素是孤立存在的（理解为单身），向集合中存储元素采用一个个元素的方式存储。
- Map中的集合，元素是成对存在的(理解为夫妻)。每个元素由键与值两部分组成，通过键可以找对所对应的值。
- Collection中的集合称为单列集合，Map中的集合称为双列集合。
- 需要注意的是，Map中的集合不能包含重复的键，值可以重复；每个键只能对应一个值。

#### HashMap

![image-20201104002454234](C:\Users\40515\AppData\Roaming\Typora\typora-user-images\image-20201104002454234.png)

结构：数组+链表+红黑树的结构。哈希桶采用数组，链表数据大于8的时候改成红黑树，当长度降到6时转成链表

##### 红黑树（java8中）

> * BST（二叉搜索树)：左边的所有节点比根节点小，右边所有节点比根节点大。查询：O(logn）,最坏O(n）（递增的时候）
> * AVL(自平衡二叉树)：左右深度差≤1的BST，读写复杂度是O(logn)（最坏也是）
>
> 因为相对于AVL平衡左右深度条件宽松很多（可以差1倍），所以插入的时候可以减少很多操作。但查询复杂度仍然是O(logn)

hashMap的长度n必须为2^k，因为计算位置的时候需要n-1 = 11...11，如下图所示：

![image-20201104002903666](C:\Users\40515\AppData\Roaming\Typora\typora-user-images\image-20201104002903666.png)



#### LinkedHashMap



#### TreeMap



#### HashTable



#### ConcurrentHashMap



### Set



#### HashSet



## 线程



### 创建

1. 继承Thread类，重写run()，无返回
2. 实现Runnable类，重写run()，无返回
3. 实现Callable接口，重写call()，有返回
4. 通过线程池来创建线程



实现Callback接口，重写call()：

>
>
>① 定义MyClass实现Callable接口；Class MyClass implements Callable
>② 重写call(),将执行的代码写入；
>③ 创建FutureTask的对象；FutureTask中定义了run(),run()内部调用了call(),并保存了call()的返回值；FutureTask futuretask = new FutureTask(newMyClass());
>④ 创建Thread的对象；Thread thread = new Thread(futuretask);//传入参数Runnable接口
>⑤ 启动线程;thread.start();[图片]
>⑥ 可通过FutureTask类的get()方法获得线程执行结束后的返回值，即call的返回值。futuretask.get();
>
>```java
>import java.util.concurrent.Callable;
>import java.util.concurrent.ExecutionException;
>import java.util.concurrent.FutureTask;
>
>public class MyThread {
>
>    public static void main(String[] args) throws InterruptedException {
>        FutureTask<Integer> task = new FutureTask<Integer>(new CallableImpl());
>        Thread thread = new Thread(task);
>        thread.start();
>        try {
>            System.out.println("task.get() returns " + task.get());
>        } catch (ExecutionException e) {
>            e.printStackTrace();
>        }
>    }
>}
>
>class CallableImpl implements Callable<Integer> {
>
>    private static Integer value = 0;
>
>    @Override
>    public Integer call() throws Exception {
>        System.out.println("执行call方法之前 value = " + value);
>        value = value.intValue() + 1;
>        System.out.println("执行call方法之后 value = " + value);
>        return value;
>    }
>}
>```
>
>







### 锁/同步



### ThreadLock(较难)



## JVM

### 内存



### GC



### 类加载过程







