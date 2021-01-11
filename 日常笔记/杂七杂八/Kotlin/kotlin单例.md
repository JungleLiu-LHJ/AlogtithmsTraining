## Object原理

kotlin

```kotlin
object SingleInstance {//使用object来声明一个单例对象
}
```

反编译成java:

```java
public final class single {
   public static final single INSTANCE;

   private single() {
   }

   static {
      single var0 = new single();
      INSTANCE = var0;
   }
}
```

---



kotlin:

```kotlin
class single {
    private var ojb = object {
        var i = 1
    }
    var ojb2 = object {
        var j = 1
    }
}
```

java:

```java
public final class single {
   private <undefinedtype> ojb = new Object() {
      private int i = 1;

      public final int getI() {
         return this.i;
      }

      public final void setI(int var1) {
         this.i = var1;
      }
   };
    
   @NotNull
   private Object ojb2 = new Object() {
      private int j = 1;

      public final int getJ() {
         return this.j;
      }

      public final void setJ(int var1) {
         this.j = var1;
      }
   };
    

   @NotNull
   public final Object getOjb2() {
      return this.ojb2;
   }

   public final void setOjb2(@NotNull Object var1) {
      Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
      this.ojb2 = var1;
   }
}
```





## Companion object 原理

kotlin:

```kotlin
class single {
    companion object {
        var instance = single()
    }
}
```

java:

```java
public final class single {
   @NotNull
   private static single instance = new single();
   public static final single.Companion Companion = new single.Companion((DefaultConstructorMarker)null);

   public static final class Companion {
      @NotNull
      public final single getInstance() {
         return single.instance;
      }

      public final void setInstance(@NotNull single var1) {
         Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
         single.instance = var1;
      }

      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
```





## 单例模式

### 1. 饿汉式

```java
//Java实现
public class SingletonDemo {
    private static SingletonDemo instance=new SingletonDemo();
    private SingletonDemo(){

    }
    public static SingletonDemo getInstance(){
        return instance;
    }
}

//Kotlin实现
object SingletonDemo{
    
}

```

>
>
>**是否 Lazy 初始化：**否
>
>**是否多线程安全：**是
>
>**实现难度：**易
>
>**描述：**这种方式比较常用，但容易产生垃圾对象。

### 2. 懒汉式

#### 非线程安全

``` java
//Java实现
public class SingletonDemo {
    private static SingletonDemo instance;
    private SingletonDemo(){}
    public static SingletonDemo getInstance(){
        if(instance==null){
            instance=new SingletonDemo();
        }
        return instance;
    }
}

//Kotlin实现
class SingletonDemo private constructor() {
    companion object {
        private var instance: SingletonDemo? = null
            get() {
                if (field == null) {
                    field = SingletonDemo()
                }
                return field
            }
        fun get(): SingletonDemo{
        //细心的小伙伴肯定发现了，这里不用getInstance作为为方法名，是因为在伴生对象声明时，内部已有getInstance方法，所以只能取其他名字
         return instance!!
        }
    }
}

```

>
>
>**是否 Lazy 初始化：**是
>
>**是否多线程安全：**否
>
>**实现难度：**易
>
>**描述：**这种方式是最基本的实现方式，这种实现最大的问题就是不支持多线程。因为没有加锁 synchronized，所以严格意义上它并不算单例模式。
>这种方式 lazy loading 很明显，不要求线程安全，在多线程不能正常工作。

#### 线程安全

```java
//Java实现
public class SingletonDemo {
    private static SingletonDemo instance;
    private SingletonDemo(){}
    public static synchronized SingletonDemo getInstance(){//使用同步锁
        if(instance==null){
            instance=new SingletonDemo();
        }
        return instance;
    }
}

//Kotlin实现
class SingletonDemo private constructor() {
    companion object {
        private var instance: SingletonDemo? = null
            get() {
                if (field == null) {
                    field = SingletonDemo()
                }
                return field
            }
        @Synchronized
        fun get(): SingletonDemo{
            return instance!!
        }
    }

}

```



### 3. 双重校验锁式

```java
//Java实现
public class SingletonDemo {
    private volatile static SingletonDemo instance;
    private SingletonDemo(){} 
    public static SingletonDemo getInstance(){
        if(instance==null){
            synchronized (SingletonDemo.class){
                if(instance==null){
                    instance=new SingletonDemo();
                }
            }
        }
        return instance;
    }
}
//kotlin实现
class SingletonDemo private constructor() {
    companion object {
        val instance: SingletonDemo by lazy {
        SingletonDemo() }
    }
}

```

>
>
>**JDK 版本：**JDK1.5 起
>
>**是否 Lazy 初始化：**是
>
>**是否多线程安全：**是
>
>**实现难度：**较复杂
>
>**描述：**这种方式采用双锁机制，安全且在多线程情况下能保持高性能。
>getInstance() 的性能对应用程序很关键。

### 4. 静态内部类

```java
//Java实现
public class Singleton {  
    private static class SingletonHolder {  
   		 private static final Singleton INSTANCE = new Singleton(); 
    }  
    private Singleton (){}  
    public static final Singleton getInstance() {  
    	return SingletonHolder.INSTANCE;  
    }  
}


//kotlin实现
class SingletonDemo private constructor() {
    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder= SingletonDemo()
    }

}

```

