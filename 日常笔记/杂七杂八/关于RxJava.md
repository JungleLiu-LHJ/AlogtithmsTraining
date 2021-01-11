## 关于RxJava



> 异步操作很关键的一点是程序的简洁性，因为在调度过程比较复杂的情况下，异步代码经常会既难写也难被读懂。 Android 创造的AsyncTask 和Handler ，其实都是为了让异步代码更加简洁。RxJava 的优势也是简洁，但它的简洁的与众不同之处在于，随着程序逻辑变得越来越复杂，它依然能够保持简洁。



> RxJava就是很方便的观察者和异步。



#### 创建被观察者

```java
Observable switcher = Observable.create(new Observable.OnSubscribe<String>(){

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("On");
                subscriber.onNext("Off");
                subscriber.onNext("On");
                subscriber.onNext("On");
                subscriber.onCompleted();
            }
        });
```



简洁模式

```java
Observable switcher=Observable.just("On","Off","On","On");
```



#### 观察者 

```java
 Subscriber light=new Subscriber<String>() {
            @Override
            public void onCompleted() {
                //被观察者的onCompleted()事件会走到这里;
                Log.d("DDDDDD","结束观察...\n");
            }

            @Override
            public void onError(Throwable e) {
                    //出现错误会调用这个方法
            }
            @Override
            public void onNext(String s) {
                //处理传过来的onNext事件
                Log.d("DDDDD","handle this---"+s)
            }
```

 

#### 订阅

```java
switcher.subscribe(light);
```

开关订阅台灯，表面上看是反了，其实是为了流式`API`调用风格，背后的原理和观察者一样，应该是台灯订阅开关。下面的就是流式`API`调用：

```java
Observable.just("On","Off","On","On")
        //在传递过程中对事件进行过滤操作
         .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s！=null;
                    }
                })
        .subscribe(mSubscriber);
```

流式`API`调用的流程就是： 创建被观察者—>对事件进行加工—...—>创建被观察者



### RxJava操作符

- Map
- FlatMap
- Filter
- Interval
- Merge
- Schedulers
- take
- toSortedList
- rxbinding



#### map

```java
 Observable.create(new Observable.just(getFilePath())
           //指定了被观察者执行的线程环境
          .subscribeOn(Schedulers.newThread())
          //将接下来执行的线程环境指定为io线程
          .observeOn(Schedulers.io())
            //使用map操作来完成类型转换
            .map(new Func1<String, Bitmap>() {
              @Override
              public Bitmap call(String s) {
                //显然自定义的createBitmapFromPath(s)方法，是一个极其耗时的操作
                  return createBitmapFromPath(s);
              }
          })
            //将后面执行的线程环境切换为主线程
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                 //创建观察者，作为事件传递的终点处理事件    
                  new Subscriber<Bitmap>() {
                        @Override
                        public void onCompleted() {
                            Log.d("DDDDDD","结束观察...\n");
                        }

                        @Override
                        public void onError(Throwable e) {
                            //出现错误会调用这个方法
                        }
                        @Override
                        public void onNext(Bitmap s) {
                            //处理事件
                            showBitmap(s)
                        }
                    );
```

被观察者输入的是图片的路径，但是观察者需要`Bitmap`。就用`Map`对数据进行转换，new Func1() 就对应了类型的转你方向，String是原类型，Bitmap是转换后的类型。在call()方法中，输入的是原类型，返回转换后的类型。





#### interval

发送事件的特点：每隔指定时间就发送事件。

用于创建Observable，跟TimerTask类似，用于周期性发送信息，是一个可以指定线程的TimerTask

每隔3s发送一次的例子：

```java
  private Subscription subscribe;
    private void start() {
        if (subscribe == null || subscribe.isUnsubscribed()) {
            subscribe = Observable.interval(3000, 3000, TimeUnit.MILLISECONDS)//延时3000 ，每间隔3000，时间单位
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            Log.e("1234", "执行一次");
                        }
                    });
        }
    }

```





### 关于Disposable和CompositeDisposable

`Disposable`是一个包含两个方法的接口：

```java
public interface Disposable {  
    void dispose();  
    boolean isDisposed();
}
```

##### `Disposable`的工作机制

```java
TestScheduler scheduler = new TestScheduler();
TestObserver<Long> o = Observable.interval(1, SECONDS, scheduler).test();
o.assertNoValues();
scheduler.advanceTimeBy(1, SECONDS);
o.assertValues(0L);
scheduler.advanceTimeBy(1, SECONDS);
o.assertValues(0L, 1L);
o.dispose();// Dispose the connection.
scheduler.advanceTimeBy(100, SECONDS);
o.assertValues(0L, 1L);
```

执行后每秒会发送一次，当`o.dispose`之后，观察者将接收不到任何信息。

上面的.`test`()用`Observable.subscribe(Observer)`创建了一个观察者并返回那个观察者,和直接用Lamda表达式是一样的

> 那么Observable.subscribe（Observer）方法在做什么？简化后，它仅调用subscribeActual（Observer），这是Observable类的抽象方法。在RxJava 2中，实现运算符的方式与RxJava 1不同。现在，所有的Observable运算符都从Observable扩展而来，并且重写了subscriptionActual（Observer）方法。例如，间隔机制是在ObservableInterval中实现的，它只是扩展了Observable <Long>。还要注意，只有一种方法可以从Observable重写，subscribeActual（Observer）。

#### 观察者

和RxJava1相比Observer只是多了一个方法的接口，有4个方法，其中3个方法和RxJava1是一样的。

- `onNext(T)` — notifies the observer with the item that can be observed
- `onError(Throwable)` — notifies the observer the error
- `onComplete()` — notifies the observer that there are no more items sent

当然，还有一个方法 `onSubscribe(Disposable)`

`onSubscribe(Disposable)`主要是Disposable 作为参数，该参数用来处理观察者和被观察者之间的连接以及检查我们是准备处理或者废弃。原文如下：

> onSubscribe gets the Disposable as a parameter which can be used for disposing the connection between the Observable and the Observer itself as well as checking whether we’re already disposed or not.



##### CompositeDisposable

一个界面的上会有多个订阅（比如有多个网络接口请求）,这时候我们需要批量取消订阅，有些人会写一个ArrayList，然后把这些上面我们返回的DisposableObserver对象加入到ArrayList中，然后当我们的界面关闭的时候，再遍历ArrayList，把里面的元素取出来一个个取消订阅。实际上RxJava 2 中有替我们考虑到这个需求。那便是CompositeDisposable类。

```java
CompositeDisposable compositeDisposable = new CompositeDisposable();
//批量添加
compositeDisposable.add(observer1);
compositeDisposable.add(observer2);
compositeDisposable.add(observer2);
//最后一次性全部取消订阅
compositeDisposable.dispose();

```



#### Flowable

Rxjava2相对于Rxjava1最大的更新就是把对背压问题的处理逻辑从Observable中抽取出来产生了新的可观察对象Flowable。

在Rxjava2中，Flowable可以看做是为了解决背压问题，在Observable的基础上优化后的产物，与Observable不处在同一组观察者模式下，Observable是ObservableSource/Observer这一组观察者模式中ObservableSource的典型实现，而Flowable是Publisher与Subscriber这一组观察者模式中Publisher的典型实现。

所以在使用Flowable的时候，可观察对象不再是Observable,而是Flowable;观察者不再是Observer，而是Subscriber。Flowable与Subscriber之间依然通过subscribe()进行关联。

虽然在Rxjava2中，Flowable是在Observable的基础上优化后的产物，Observable能解决的问题Flowable也都能解决，但是并不代表Flowable可以完全取代Observable,在使用的过程中，并不能抛弃Observable而只用Flowable。

由于基于Flowable发射的数据流，以及对数据加工处理的各操作符都添加了背压支持，附加了额外的逻辑，其运行效率要比Observable慢得多。

**只有在需要处理背压问题时，才需要使用Flowable。**

由于只有在上下游运行在不同的线程中，且上游发射数据的速度大于下游接收处理数据的速度时，才会产生背压问题；
 所以，如果能够确定：
 1、上下游运行在同一个线程中，
 2、上下游工作在不同的线程中，但是下游处理数据的速度不慢于上游发射数据的速度，
 3、上下游工作在不同的线程中，但是数据流中只有一条数据
 则不会产生背压问题，就没有必要使用Flowable，以免影响性能。



详细可以看：https://www.jianshu.com/p/ff8167c1d191/



### RxJava与Retrofit结合

参考：http://gank.io/post/56e80c2c677659311bed9841





参考文献

《这可能是最好的RxJava 2.x 教程（完结版）》 https://www.jianshu.com/p/0cd258eecf60

《关于RxJava最友好的文章（初级篇）》https://zhuanlan.zhihu.com/p/23584382

