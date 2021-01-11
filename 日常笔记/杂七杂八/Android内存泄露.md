# Android内存泄露

**一般内存泄漏(traditional memory leak)**的原因是：由忘记释放分配的内存导致的。（译者注：`Cursor`忘记关闭等)
**逻辑内存泄漏(logical memory leak)**的原因是：当应用不再需要这个对象，当仍未释放该对象的所有引用。



内存泄露几种情况：

1. Static Activity

   ```java
       static Activity activity;
       
       void setStaticActivity() {
         activity = this;
       }
       
       View saButton = findViewById(R.id.sa_button);
       saButton.setOnClickListener(new View.OnClickListener() {
         @Override public void onClick(View v) {
           setStaticActivity();
           nextActivity();
         }
       });
   ```

   > 在类中定义了静态`Activity`变量，把当前运行的`Activity`实例赋值于这个静态变量。
   > 如果这个静态变量在`Activity`生命周期结束后没有清空，就导致内存泄漏。因为static变量是贯穿这个应用的生命周期的，所以被泄漏的`Activity`就会一直存在于应用的进程中，不会被垃圾回收器回收。

2. Static View

   一般不会用到

3. Inner Classes

   假设`Activity`中有个[内部类](https://link.jianshu.com/?t=https://github.com/NimbleDroid/Memory-Leaks/blob/master/app/src/main/java/com/nimbledroid/memoryleaks/MainActivity.java#L126)，这样做可以提高可读性和封装性。将如我们创建一个内部类，而且持有一个静态变量的引用

   ```java
        private static Object inner;
          
          void createInnerClass() {
           class InnerClass {
           }
           inner = new InnerClass();
       }
       
       View icButton = findViewById(R.id.ic_button);
       icButton.setOnClickListener(new View.OnClickListener() {
           @Override public void onClick(View v) {
               createInnerClass();
               nextActivity();
           }
       });
   ```

4. Anonymous Classes

   匿名类也维护了外部类的引用。所以内存泄漏很容易发生

   ```java
     void startAsyncTask() {
           new AsyncTask<Void, Void, Void>() {
               @Override protected Void doInBackground(Void... params) {
                   while(true);
               }
           }.execute();
       }
       
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       View aicButton = findViewById(R.id.at_button);
       aicButton.setOnClickListener(new View.OnClickListener() {
           @Override public void onClick(View v) {
               startAsyncTask();
               nextActivity();
           }
       });
   ```

   > [当你在`Activity`中定义了匿名的`AsyncTsk`](https://link.jianshu.com?t=https://github.com/NimbleDroid/Memory-Leaks/blob/master/app/src/main/java/com/nimbledroid/memoryleaks/MainActivity.java#L102)
   > 。当异步任务在后台执行耗时任务期间，`Activity`不幸被销毁了（译者注：用户退出，系统回收），这个被`AsyncTask`持有的`Activity`实例就不会被垃圾回收器回收，直到异步任务结束。
   > 作者：豆沙包67链接：https://www.jianshu.com/p/ac00e370f83d来源：简书著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

5. Handler

   定义匿名的`Runnable`，用匿名类`Handler`执行。`Runnable`内部类会持有外部类的隐式引用，被传递到`Handler`的消息队列`MessageQueue`中，在`Message`消息没有被处理之前，`Activity`实例不会被销毁了，于是导致内存泄漏。

   ```java
       void createHandler() {
           new Handler() {
               @Override public void handleMessage(Message message) {
                   super.handleMessage(message);
               }
           }.postDelayed(new Runnable() {
               @Override public void run() {
                   while(true);
               }
           }, Long.MAX_VALUE >> 1);
       }
       
       
       View hButton = findViewById(R.id.h_button);
       hButton.setOnClickListener(new View.OnClickListener() {
           @Override public void onClick(View v) {
               createHandler();
               nextActivity();
           }
       });
   ```

6. Thread

   和上面一样，匿名内部类

7. 















