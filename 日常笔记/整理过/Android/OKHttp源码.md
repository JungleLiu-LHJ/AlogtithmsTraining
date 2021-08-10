## OKHttp

### 做了什么事

* 请求格式
* 连接
* https支持，验证证书
* 各种版本http支持
* 连接的复用 
* 重连接



流程：

![okhttp1](.\okhttp1.jpg)





### 变量解析

```kotlin
open class OkHttpClient internal constructor(
  builder: Builder
) : Cloneable, Call.Factory, WebSocket.Factory {
	//调度器
  @get:JvmName("dispatcher") val dispatcher: Dispatcher = builder.dispatcher
	//连接池，连接的复用
  @get:JvmName("connectionPool") val connectionPool: ConnectionPool = builder.connectionPool

 //拦截器
  @get:JvmName("interceptors") val interceptors: List<Interceptor> =
      builder.interceptors.toImmutableList()

  /**
   * Returns an immutable list of interceptors that observe a single network request and response.
   * These interceptors must call [Interceptor.Chain.proceed] exactly once: it is an error for
   * a network interceptor to short-circuit or repeat a network request.
   */
  @get:JvmName("networkInterceptors") val networkInterceptors: List<Interceptor> =
      builder.networkInterceptors.toImmutableList()

    //listenner的工厂
  @get:JvmName("eventListenerFactory") val eventListenerFactory: EventListener.Factory =
      builder.eventListenerFactory
 //连接失败和请求失败的时候是否重试
  @get:JvmName("retryOnConnectionFailure") val retryOnConnectionFailure: Boolean =
      builder.retryOnConnectionFailure
	//对于无权访问的资源的回调，可以进行token的刷新
  @get:JvmName("authenticator") val authenticator: Authenticator = builder.authenticator
	//是否要继续重定向 是：按照要求直接进行请求。默认为true 否：把301（需要重定向）结果返回
  @get:JvmName("followRedirects") val followRedirects: Boolean = builder.followRedirects
	//协议切换重定向（http返回需要https） 是：按照要求直接进行https请求。默认为true 否：把（需要重定向）结果返回
  @get:JvmName("followSslRedirects") val followSslRedirects: Boolean = builder.followSslRedirects
	//存储cookie的，默认不提供。cookie一般不用使用
  @get:JvmName("cookieJar") val cookieJar: CookieJar = builder.cookieJar
	//缓存
  @get:JvmName("cache") val cache: Cache? = builder.cache
	//利用原生方法查找dns地址
  @get:JvmName("dns") val dns: Dns = builder.dns
	//代理配置
  @get:JvmName("proxy") val proxy: Proxy? = builder.proxy

  @get:JvmName("proxySelector") val proxySelector: ProxySelector =
      when {
        // Defer calls to ProxySelector.getDefault() because it can throw a SecurityException.
        builder.proxy != null -> NullProxySelector
        else -> builder.proxySelector ?: ProxySelector.getDefault() ?: NullProxySelector
      }
//代理的时候的权限重连
  @get:JvmName("proxyAuthenticator") val proxyAuthenticator: Authenticator =
      builder.proxyAuthenticator
//创建socket的工厂
  @get:JvmName("socketFactory") val socketFactory: SocketFactory = builder.socketFactory

  private val sslSocketFactoryOrNull: SSLSocketFactory?
	//加密下的SSL连接工厂
  @get:JvmName("sslSocketFactory") val sslSocketFactory: SSLSocketFactory
    get() = sslSocketFactoryOrNull ?: throw IllegalStateException("CLEARTEXT-only client")
	//证书的验证器，x509是一种证书格式
  @get:JvmName("x509TrustManager") val x509TrustManager: X509TrustManager?
  //加密套件配置
  @get:JvmName("connectionSpecs") val connectionSpecs: List<ConnectionSpec> =
      builder.connectionSpecs
	//协议，比如HTTP1 ，HTTP1.1，HTTP2等
  @get:JvmName("protocols") val protocols: List<Protocol> = builder.protocols

   //下面三个都是关于证书验证的
    //主机名验证，比较域名
  @get:JvmName("hostnameVerifier") val hostnameVerifier: HostnameVerifier = builder.hostnameVerifier
 // 自己配置校验的根证书hash，一般不用
  @get:JvmName("certificatePinner") val certificatePinner: CertificatePinner
 //操作x509TrustManager进行验证的
  @get:JvmName("certificateChainCleaner") val certificateChainCleaner: CertificateChainCleaner?

    //心跳机制的时间
  /** Web socket and HTTP/2 ping interval (in milliseconds). By default pings are not sent. */
  @get:JvmName("pingIntervalMillis") val pingIntervalMillis: Int = builder.pingInterval

  /**
   * Minimum outbound web socket message size (in bytes) that will be compressed.
   * The default is 1024 bytes.
   */
  @get:JvmName("minWebSocketMessageToCompress")
  val minWebSocketMessageToCompress: Long = builder.minWebSocketMessageToCompress

  val routeDatabase: RouteDatabase = builder.routeDatabase ?: RouteDatabase()
```







### 关键代码getResponseWithInterceptorChain()：





```kotlin
internal fun getResponseWithInterceptorChain(): Response {
  // Build a full stack of interceptors.
  val interceptors = mutableListOf<Interceptor>()
  interceptors += client.interceptors
  interceptors += RetryAndFollowUpInterceptor(client)
  interceptors += BridgeInterceptor(client.cookieJar)
  interceptors += CacheInterceptor(client.cache)
  interceptors += ConnectInterceptor
  if (!forWebSocket) {
    interceptors += client.networkInterceptors
  }
  interceptors += CallServerInterceptor(forWebSocket)

 


  //拦截器的链的包起来   
  val chain = RealInterceptorChain(
      call = this,
      interceptors = interceptors,
      index = 0,
      exchange = null,
      request = originalRequest,
      connectTimeoutMillis = client.connectTimeoutMillis,
      readTimeoutMillis = client.readTimeoutMillis,
      writeTimeoutMillis = client.writeTimeoutMillis
  )

    
  //让链条开始转起来
  var calledNoMoreExchanges = false
  try {
    val response = chain.proceed(originalRequest)//开始执行
    if (isCanceled()) {
      response.closeQuietly()
      throw IOException("Canceled")
    }
    return response
  } catch (e: IOException) {
    calledNoMoreExchanges = true
    throw noMoreExchanges(e) as Throwable
  } finally {
    if (!calledNoMoreExchanges) {
      noMoreExchanges(null)
    }
  }
}
```



如何让链条转起来的呢？？

每次调用intercepter的时候，会调用call方法new 一个RealInterceptorChain，参数一样，只是让里面的index=index+1。然后执行Intercepter.intercepter(RealInterceptorChain()),里面再执行RealInterceptorChain.proceed()。如下所示：

```kotlin
class RealInterceptorChain{
...
override fun proceed(request: Request): Response {

  // Call the next interceptor in the chain.
  val next = copy(index = index + 1, request = request) //其实是new 一个RealInterceptorChain，index+1，传入request
  val interceptor = interceptors[index]

  @Suppress("USELESS_ELVIS")
  val response = interceptor.intercept(next) //执行下一个intercepter，并且把新的RealInterceptorChain传进去

  return response
}
}

class RetryAndFollowUpInterceptor(private val client: OkHttpClient) : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
  		...
      //前置工作
          response = realChain.proceed(request)
 
          ...
           //后置工作
      }
    }
  }
```









#### 链式调用：

`RetryAndFollowUpInterceptor`(client) ---> `BridgeInterceptor`(client.cookieJar)--->`CacheInterceptor`(client.cache)---->`ConnectInterceptor`--->`CallServerInterceptor`(forWebSocket)



前置-->中置（调用下一个链并等待返回）-->后置-->返回

1. RetryAndFollowUpInterceptor：前置：初始化工作       后置：请求失败的时候重试，以及重定向的自动请求
2. `BridgeInterceptor`：前置：content-Length的计算添加、gzip的支持和压缩等        后置：移除content-Length、gzip的解包等
3. `CacheInterceptor`：负责Cache处理。前置：有可用缓存就用缓存            后置： 看是否要存到缓存中
4.  `ConnectInterceptor`：前置：最难最复杂的，建立连接（TCP或者TLS连接），并创建HttpCodec用于HTTP的编码解码
5. `CallServerInterceptor`负责实质的请求和响应的I/O操作，从Socket写入请求数据和从Socket读取响应数据



#### RetryAndFollowUpInterceptor

前置：初始化工作       后置：请求失败的时候重试，以及重定向的自动请求

```kotlin
override fun intercept(chain: Interceptor.Chain): Response {
  val realChain = chain as RealInterceptorChain
  var request = chain.request
  val call = realChain.call
  var followUpCount = 0
  var priorResponse: Response? = null
  var newExchangeFinder = true
  var recoveredFailures = listOf<IOException>()
  while (true) { //不断循环是为了请求不到的时候不断进行重试
    call.enterNetworkInterceptorExchange(request, newExchangeFinder) //寻找可用的连接，newExchangeFinder为true会new一个ExchangeFinder

    var response: Response
    var closeActiveExchange = true
    try {
      if (call.isCanceled()) {
        throw IOException("Canceled")
      }

      try {
        response = realChain.proceed(request)
        newExchangeFinder = true //
      } catch (e: RouteException) {//某条链接失败
        // The attempt to connect via a route failed. The request will not have been sent.
        if (!recover(e.lastConnectException, call, request, requestSendStarted = false)) { //做很多判断是否需要恢复
          throw e.firstConnectException.withSuppressed(recoveredFailures)
        } else {
          recoveredFailures += e.firstConnectException
        }
        newExchangeFinder = false //这里为false，创建新链接的时候就不会新建ExchangeFinder
        continue
      } catch (e: IOException) { //链接超时
        // An attempt to communicate with a server failed. The request may have been sent.
        if (!recover(e, call, request, requestSendStarted = e !is ConnectionShutdownException)) {
          throw e.withSuppressed(recoveredFailures)
        } else {
          recoveredFailures += e
        }
        newExchangeFinder = false
        continue
      }

      // Attach the prior response if it exists. Such responses never have a body.
        //下面是需要进行重定向的，重定向会重新建立一次请求
      if (priorResponse != null) {
        response = response.newBuilder()
            .priorResponse(priorResponse.newBuilder()
                .body(null)
                .build())
            .build()
      }

      val exchange = call.interceptorScopedExchange
      val followUp = followUpRequest(response, exchange) //根据返回的code来决定是否需要重定向

	 ...

      request = followUp //赋值给request，下一轮循环的时候用该request重新请求
      priorResponse = response
    } finally {
      call.exitNetworkInterceptorExchange(closeActiveExchange)
    }
  }
}
```





#### BridgeInterceptor

做一些host、content-Length、压缩等工作

```kotlin
class BridgeInterceptor(private val cookieJar: CookieJar) : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val userRequest = chain.request()
    val requestBuilder = userRequest.newBuilder()

    val body = userRequest.body
    if (body != null) {
      val contentType = body.contentType()
      if (contentType != null) {
        requestBuilder.header("Content-Type", contentType.toString())
      }

      val contentLength = body.contentLength()
      if (contentLength != -1L) {
        requestBuilder.header("Content-Length", contentLength.toString())
        requestBuilder.removeHeader("Transfer-Encoding")
      } else {
        requestBuilder.header("Transfer-Encoding", "chunked")
        requestBuilder.removeHeader("Content-Length")
      }
    }

    if (userRequest.header("Host") == null) {
      requestBuilder.header("Host", userRequest.url.toHostHeader())
    }

    if (userRequest.header("Connection") == null) {
      requestBuilder.header("Connection", "Keep-Alive")
    }

    // If we add an "Accept-Encoding: gzip" header field we're responsible for also decompressing
    // the transfer stream.
    var transparentGzip = false
    if (userRequest.header("Accept-Encoding") == null && userRequest.header("Range") == null) {
      transparentGzip = true
      requestBuilder.header("Accept-Encoding", "gzip") //默认用gzip进行压缩
    }

    val cookies = cookieJar.loadForRequest(userRequest.url)
    if (cookies.isNotEmpty()) {
      requestBuilder.header("Cookie", cookieHeader(cookies))
    }

    if (userRequest.header("User-Agent") == null) {
      requestBuilder.header("User-Agent", userAgent)
    }

    val networkResponse = chain.proceed(requestBuilder.build()) //中置

    cookieJar.receiveHeaders(userRequest.url, networkResponse.headers)

    val responseBuilder = networkResponse.newBuilder()
        .request(userRequest)

    if (transparentGzip &&
        "gzip".equals(networkResponse.header("Content-Encoding"), ignoreCase = true) &&
        networkResponse.promisesBody()) {
      val responseBody = networkResponse.body
      if (responseBody != null) {
        val gzipSource = GzipSource(responseBody.source())
        val strippedHeaders = networkResponse.headers.newBuilder()
            .removeAll("Content-Encoding")
            .removeAll("Content-Length")
            .build()
        responseBuilder.headers(strippedHeaders)
        val contentType = networkResponse.header("Content-Type")
        responseBuilder.body(RealResponseBody(contentType, -1L, gzipSource.buffer()))
      }
    }

    return responseBuilder.build()
  }
```







#### CacheInterceptor

前置工作：选取缓存  中置：传输  后置：如果结果可以缓存则缓存起来

```kotlin
class CacheInterceptor(internal val cache: Cache?) : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val call = chain.call()
    val cacheCandidate = cache?.get(chain.request())

    val now = System.currentTimeMillis()

    val strategy = CacheStrategy.Factory(now, chain.request(), cacheCandidate).compute()
    val networkRequest = strategy.networkRequest
    val cacheResponse = strategy.cacheResponse

    cache?.trackResponse(strategy)
    val listener = (call as? RealCall)?.eventListener ?: EventListener.NONE

    if (cacheCandidate != null && cacheResponse == null) {
      // The cache candidate wasn't applicable. Close it.
      cacheCandidate.body?.closeQuietly()
    }

      //查找缓存中有没有数据，有的话直接返回，没有的话再取
    // If we're forbidden from using the network and the cache is insufficient, fail.
    if (networkRequest == null && cacheResponse == null) {
      return Response.Builder()
          .request(chain.request())
          .protocol(Protocol.HTTP_1_1)
          .code(HTTP_GATEWAY_TIMEOUT)
          .message("Unsatisfiable Request (only-if-cached)")
          .body(EMPTY_RESPONSE)
          .sentRequestAtMillis(-1L)
          .receivedResponseAtMillis(System.currentTimeMillis())
          .build().also {
            listener.satisfactionFailure(call, it)
          }
    }

    // If we don't need the network, we're done.
    if (networkRequest == null) {
      return cacheResponse!!.newBuilder()
          .cacheResponse(stripBody(cacheResponse))
          .build().also {
            listener.cacheHit(call, it)
          }
    }

    if (cacheResponse != null) {
      listener.cacheConditionalHit(call, cacheResponse)
    } else if (cache != null) {
      listener.cacheMiss(call)
    }

    var networkResponse: Response? = null
    try {
      networkResponse = chain.proceed(networkRequest) //中置工作
    } finally {
      // If we're crashing on I/O or otherwise, don't leak the cache body.
      if (networkResponse == null && cacheCandidate != null) {
        cacheCandidate.body?.closeQuietly()
      }
    }

      //下面是把缓存存起来
    // If we have a cache response too, then we're doing a conditional get.
    if (cacheResponse != null) {
      if (networkResponse?.code == HTTP_NOT_MODIFIED) {
        val response = cacheResponse.newBuilder()
            .headers(combine(cacheResponse.headers, networkResponse.headers))
            .sentRequestAtMillis(networkResponse.sentRequestAtMillis)
            .receivedResponseAtMillis(networkResponse.receivedResponseAtMillis)
            .cacheResponse(stripBody(cacheResponse))
            .networkResponse(stripBody(networkResponse))
            .build()

        networkResponse.body!!.close()

        // Update the cache after combining headers but before stripping the
        // Content-Encoding header (as performed by initContentStream()).
        cache!!.trackConditionalCacheHit()
        cache.update(cacheResponse, response)
        return response.also {
          listener.cacheHit(call, it)
        }
      } else {
        cacheResponse.body?.closeQuietly()
      }
    }

    val response = networkResponse!!.newBuilder()
        .cacheResponse(stripBody(cacheResponse))
        .networkResponse(stripBody(networkResponse))
        .build()

    if (cache != null) {
      if (response.promisesBody() && CacheStrategy.isCacheable(response, networkRequest)) {
        // Offer this request to the cache.
        val cacheRequest = cache.put(response)
        return cacheWritingResponse(cacheRequest, response).also {
          if (cacheResponse != null) {
            // This will log a conditional cache miss only.
            listener.cacheMiss(call)
          }
        }
      }

      if (HttpMethod.invalidatesCache(networkRequest.method)) {
        try {
          cache.remove(networkRequest)
        } catch (_: IOException) {
          // The cache cannot be written.
        }
      }
    }

    return response
  }
```





#### ConnectInterceptor

```kotlin
object ConnectInterceptor : Interceptor {
  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val realChain = chain as RealInterceptorChain
      
    val exchange = realChain.call.initExchange(chain) //重要的一行
      
    val connectedChain = realChain.copy(exchange = exchange)
    return connectedChain.proceed(realChain.request)
  }
```



```kotlin
internal fun initExchange(chain: RealInterceptorChain): Exchange {
  synchronized(connectionPool) {
    check(!noMoreExchanges) { "released" }
    check(exchange == null)
  }

  val codec = exchangeFinder!!.find(client, chain) //寻找codec
  val result = Exchange(this, eventListener, exchangeFinder!!, codec)
  this.interceptorScopedExchange = result

  synchronized(connectionPool) {
    this.exchange = result
    this.exchangeRequestDone = false
    this.exchangeResponseDone = false
    return result
  }
}


  fun find(
    client: OkHttpClient,
    chain: RealInterceptorChain
  ): ExchangeCodec {
    try {
      val resultConnection = findHealthyConnection(
          connectTimeout = chain.connectTimeoutMillis,
          readTimeout = chain.readTimeoutMillis,
          writeTimeout = chain.writeTimeoutMillis,
          pingIntervalMillis = client.pingIntervalMillis,
          connectionRetryEnabled = client.retryOnConnectionFailure,
          doExtensiveHealthChecks = chain.request.method != "GET"
      )  //寻找健康的链接
      return resultConnection.newCodec(client, chain)
    } catch (e: RouteException) {
      trackFailure(e.lastConnectException)
      throw e
    } catch (e: IOException) {
      trackFailure(e)
      throw RouteException(e)
    }
  }

  private fun findHealthyConnection(
    connectTimeout: Int,
    readTimeout: Int,
    writeTimeout: Int,
    pingIntervalMillis: Int,
    connectionRetryEnabled: Boolean,
    doExtensiveHealthChecks: Boolean
  ): RealConnection {
    while (true) {//寻找链接并不断判断是否是健康的
      val candidate = findConnection(
          connectTimeout = connectTimeout,
          readTimeout = readTimeout,
          writeTimeout = writeTimeout,
          pingIntervalMillis = pingIntervalMillis,
          connectionRetryEnabled = connectionRetryEnabled
      )

      // Confirm that the connection is good. If it isn't, take it out of the pool and start again.
      if (!candidate.isHealthy(doExtensiveHealthChecks)) {
        candidate.noNewExchanges()
        continue
      }

      return candidate
    }
  }
```





findConnection有5个寻找链接：

1. 直接拿上次建立的链接
2. 从链接池里面找可用的链接（都是不可做链接合并(connection coalescing )的链接（只有HTTP2支持connection coalescing ），这里只能取到HTTP1的连接）
3. 从链接池里寻找加了routes的可用的链接（包含链接合并的链接）
4. 自己建立链接
5. 建立后再次寻找有无可用的多路复用链接（防止并发）



其中，包含了route的

> 

```kotlin
private fun findConnection(
  connectTimeout: Int,
  readTimeout: Int,
  writeTimeout: Int,
  pingIntervalMillis: Int,
  connectionRetryEnabled: Boolean
): RealConnection {
  var foundPooledConnection = false
  var result: RealConnection? = null
  var selectedRoute: Route? = null
  var releasedConnection: RealConnection?
  val toClose: Socket?
  synchronized(connectionPool) {
    if (call.isCanceled()) throw IOException("Canceled")

    releasedConnection = call.connection
    toClose = if (call.connection != null &&
        (call.connection!!.noNewExchanges || !call.connection!!.supportsUrl(address.url))) {
      call.releaseConnectionNoEvents()
    } else {
      null
    }

    if (call.connection != null) {
      // We had an already-allocated connection and it's good.
      result = call.connection
      releasedConnection = null
    }

    if (result == null) {
      // The connection hasn't had any problems for this call.
      refusedStreamCount = 0
      connectionShutdownCount = 0
      otherFailureCount = 0

      // Attempt to get a connection from the pool.
      if (connectionPool.callAcquirePooledConnection(address, call, null, false)) {//只能拿到HTTP1的链接
        foundPooledConnection = true
        result = call.connection
      } else if (nextRouteToTry != null) {
        selectedRoute = nextRouteToTry
        nextRouteToTry = null
      }
    }
  }
  toClose?.closeQuietly()

  if (releasedConnection != null) {
    eventListener.connectionReleased(call, releasedConnection!!)
  }
  if (foundPooledConnection) {
    eventListener.connectionAcquired(call, result!!)
  }
  if (result != null) {
    // If we found an already-allocated or pooled connection, we're done.
    return result!!
  }

  // If we need a route selection, make one. This is a blocking operation.
  var newRouteSelection = false
  if (selectedRoute == null && (routeSelection == null || !routeSelection!!.hasNext())) {
    var localRouteSelector = routeSelector //一个selector包含多个routeSelection,每个selection包含多个route
    if (localRouteSelector == null) {
      localRouteSelector = RouteSelector(address, call.client.routeDatabase, call, eventListener)
      this.routeSelector = localRouteSelector
    }
    newRouteSelection = true
    routeSelection = localRouteSelector.next()
  }

  var routes: List<Route>? = null
  synchronized(connectionPool) {
    if (call.isCanceled()) throw IOException("Canceled")

    if (newRouteSelection) {
      // Now that we have a set of IP addresses, make another attempt at getting a connection from
      // the pool. This could match due to connection coalescing.
      routes = routeSelection!!.routes
      if (connectionPool.callAcquirePooledConnection(address, call, routes, false)) { //第二次拿链接，加上了routes的链接，这次可以取到HTTP2的链接
        foundPooledConnection = true
        result = call.connection
      }
    }

    if (!foundPooledConnection) {
      if (selectedRoute == null) {
        selectedRoute = routeSelection!!.next()
      }

      // Create a connection and assign it to this allocation immediately. This makes it possible
      // for an asynchronous cancel() to interrupt the handshake we're about to do.
      result = RealConnection(connectionPool, selectedRoute!!) //创建连接对象
      connectingConnection = result
    }
  }

  // If we found a pooled connection on the 2nd time around, we're done.
  if (foundPooledConnection) {
    eventListener.connectionAcquired(call, result!!)
    return result!!
  }

  // Do TCP + TLS handshakes. This is a blocking operation.
    //没拿到连接，则进行连接
  result!!.connect(
      connectTimeout,
      readTimeout,
      writeTimeout,
      pingIntervalMillis,
      connectionRetryEnabled,
      call,
      eventListener
  )
  call.client.routeDatabase.connected(result!!.route())

  var socket: Socket? = null
  synchronized(connectionPool) { //下面是为了防止并发创建了两个可重用的多路复用链接，所以再检查一遍，创建过的话就用这个并抛弃刚才创建的
    connectingConnection = null
    // Last attempt at connection coalescing, which only occurs if we attempted multiple
    // concurrent connections to the same host.
    if (connectionPool.callAcquirePooledConnection(address, call, routes, true)) {
      // We lost the race! Close the connection we created and return the pooled connection.
      result!!.noNewExchanges = true
      socket = result!!.socket()
      result = call.connection

      // It's possible for us to obtain a coalesced connection that is immediately unhealthy. In
      // that case we will retry the route we just successfully connected with.
      nextRouteToTry = selectedRoute
    } else {
      connectionPool.put(result!!)  //把连接放到连接池里面 
      call.acquireConnectionNoEvents(result!!)
    }
  }
  socket?.closeQuietly()

  eventListener.connectionAcquired(call, result!!)
  return result!!
}
```



#### 

上面讲了需要拿可用的连接，那么如何判断连接是否可用：

1. 还接受新的请求、链接请求数量没超限（HTTP数量1，HTTP数量4）
2. 同样的路线连到同一个主机（一系列参数需要相等）



```kotlin
internal fun isEligible(address: Address, routes: List<Route>?): Boolean {
  // 链接请求数量没超限、还接受新的请求
  if (calls.size >= allocationLimit || noNewExchanges) return false

  // 连接的route要相等，比如连接的跳转
  if (!this.route.address.equalsNonHost(address)) return false

  // 判断host是否相等，不相等下面继续判断
  if (address.url.host == this.route().address.url.host) {
    return true // This connection is a perfect match.
  }

	// host不相等，下面继续判断ip,证书等相不相等，满足的话也可以重用
  // 1. This connection must be HTTP/2.
  if (http2Connection == null) return false

  // 2. The routes must share an IP address. 使用同一个ip
  if (routes == null || !routeMatchesAny(routes)) return false

  // 3. This connection's server certificate's must cover the new host.  证书也要一样
  if (address.hostnameVerifier !== OkHostnameVerifier) return false
  if (!supportsUrl(address.url)) return false

  // 4. Certificate pinning must match the host. certificatePinner
  try {
    address.certificatePinner!!.check(address.url.host, handshake()!!.peerCertificates)
  } catch (_: SSLPeerUnverifiedException) {
    return false
  }

  return true // The caller's address can be carried by this connection.
}

 internal fun equalsNonHost(that: Address): Boolean {
    return this.dns == that.dns &&
        this.proxyAuthenticator == that.proxyAuthenticator &&
        this.protocols == that.protocols &&
        this.connectionSpecs == that.connectionSpecs &&
        this.proxySelector == that.proxySelector &&
        this.proxy == that.proxy &&
        this.sslSocketFactory == that.sslSocketFactory &&
        this.hostnameVerifier == that.hostnameVerifier &&
        this.certificatePinner == that.certificatePinner &&
        this.url.port == that.url.port
  }
```



#### CallServerInterceptor

发请求和读响应，主要做IO操作