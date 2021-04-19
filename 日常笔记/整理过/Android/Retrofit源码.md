## Retrofit



### 用法

1. 创建⼀个 interface 作为 Web Service 的请求集合，在⾥⾯⽤注解 （Annotation）写⼊需要配置的请求⽅法 

   ```kotlin
   interface GitHubService {
       @GET("users/{user}/repos")
       fun listRepos(@Path("user") user: String?): Call<List<Repo>>
   }
   ```

2. 在正式代码⾥⽤ Retrofit 创建出 interface 的实例 

   ```kotlin
   val retrofit = Retrofit.Builder()
       .baseUrl("https://api.github.com/")
       .build()
   
   val service = retrofit.create(GitHubService::class.java)
   ```

3. 调⽤创建出的 Service 实例的对应⽅法，创建出相应的可以⽤来发起⽹络请求的 Call 对象 

   ``` kotlin
   val repos: Call<List<Repo>> = service.listRepos("octocat")
   ```

4.  使⽤ Call.execute() 或者 Call.enqueue() 来发起请求

   ```kotlin
   repos.enqueue(object : Callback<List<Repo>?> {
       override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {
       ...
       }
   
       override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
   	...
       }
   })
   ```





### 源码

从`val service = retrofit.create(GitHubService::class.java)`开始：



```java
public <T> T create(final Class<T> service) {
  validateServiceInterface(service); //为了验证，看下文
    
   //动态代理
  return (T) Proxy.newProxyInstance(service.getClassLoader(),//就是拿一个类加载器来，不关键
                                    new Class<?>[] { service }, //动态代理接口
      								new InvocationHandler() { //动态代理，细节如下
        private final Platform platform = Platform.get();//获取当前版本，如JDK1.8
        private final Object[] emptyArgs = new Object[0];

        @Override public @Nullable Object invoke(Object proxy, Method method,
            @Nullable Object[] args) throws Throwable {
          // If the method is a method from Object then defer to normal invocation.
            
            
          if (method.getDeclaringClass() == Object.class) { //原始方法
            return method.invoke(this, args);
          }
          if (platform.isDefaultMethod(method)) { //不同平台
            return platform.invokeDefaultMethod(method, service, proxy, args);
          }
          //关键：  
          return loadServiceMethod(method).invoke(args != null ? args : emptyArgs);
        }
                                        
                                        
      });
}
```

> 用了动态代理
>
> 类似于：
>
> ```java
> public class ProxyGithubService implements GitHubService{
>     InvocationHandler invocationHandler = new InvocationHandler() {
>         private final Platform platform = Platform.get();
>         private final Object[] emptyArgs = new Object[0];
> 
>         @Override public @Nullable Object invoke(Object proxy, Method method,
>                                                  @Nullable Object[] args) throws Throwable {
>             // If the method is a method from Object then defer to normal invocation.
>             if (method.getDeclaringClass() == Object.class) {
>                 return method.invoke(this, args);
>             }
>             if (platform.isDefaultMethod(method)) {
>                 return platform.invokeDefaultMethod(method, service, proxy, args);
>             }
>             return loadServiceMethod(method).invoke(args != null ? args : emptyArgs);
>         }
>     };
> 
> 
>     @NotNull
>     @Override
>     public Call<List<Repo>> listRepos(@Nullable String user) {
>         Method method = GitHubService.class.getDeclaredMethod("listRepos",String.class);
>         return invocationHandler.invoke(this,method,user);
>     }
> }
> ```
>
> 





validateServiceInterface:

```java
private void validateServiceInterface(Class<?> service) {
  if (!service.isInterface()) {
    throw new IllegalArgumentException("API declarations must be interfaces.");
  } //验证是否是接口

    
  //接口和父接口中不能有泛型
  Deque<Class<?>> check = new ArrayDeque<>(1);
  check.add(service); //可能有父接口
    
  while (!check.isEmpty()) {
    Class<?> candidate = check.removeFirst();
    if (candidate.getTypeParameters().length != 0) { //不许是泛型
      StringBuilder message = new StringBuilder("Type parameters are unsupported on ")
          .append(candidate.getName());
      if (candidate != service) {
        message.append(" which is an interface of ")
            .append(service.getName());
      }
      throw new IllegalArgumentException(message.toString());
    }
    Collections.addAll(check, candidate.getInterfaces());
  }
    
    
    

  if (validateEagerly) { //debug开关
      //验证每个方法是否可以加载
    Platform platform = Platform.get();
    for (Method method : service.getDeclaredMethods()) {
      if (!platform.isDefaultMethod(method) && !Modifier.isStatic(method.getModifiers())) 
  	  {
        loadServiceMethod(method); //验证是否能加载
      }
    }
      
  }
    
    
}
```



loadServiceMethod:

```java
ServiceMethod<?> loadServiceMethod(Method method) {
  ServiceMethod<?> result = serviceMethodCache.get(method); // serviceMethodCache是一个map，缓存的作用
  if (result != null) return result;

  synchronized (serviceMethodCache) { //没取到就新建一个
    result = serviceMethodCache.get(method);
    if (result == null) {
      result = ServiceMethod.parseAnnotations(this, method);
      serviceMethodCache.put(method, result);
    }
  }
  return result;
}
```







enqueue

```java
@Override public void enqueue(final Callback<T> callback) {
 			...
        call = rawCall = createRawCall();//获取okhttp的call
     		...

  call.enqueue(new okhttp3.Callback() {
    @Override public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse) {
      Response<T> response;
      try {
        response = parseResponse(rawResponse);//用于解析
      } catch (Throwable e) {
        throwIfFatal(e);
        callFailure(e);
        return;
      }

      try {
        callback.onResponse(OkHttpCall.this, response); //最外面的callback
      } catch (Throwable t) {
        throwIfFatal(t);
        t.printStackTrace(); 
      }
    }

    @Override public void onFailure(okhttp3.Call call, IOException e) {
      callFailure(e);
    }

    private void callFailure(Throwable e) {
      try {
        callback.onFailure(OkHttpCall.this, e); //最外面的onFailure
      } catch (Throwable t) {
        throwIfFatal(t);
        t.printStackTrace(); // TODO this is not great
      }
    }
  });
}
```