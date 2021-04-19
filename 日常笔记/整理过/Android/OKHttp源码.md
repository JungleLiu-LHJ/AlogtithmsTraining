## OKHttp





### 关键代码：

   [你是谁](#傻狍子)

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
    val response = chain.proceed(originalRequest)
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





#### 链式调用：

`RetryAndFollowUpInterceptor`(client) ---> `BridgeInterceptor`(client.cookieJar)--->`CacheInterceptor`(client.cache)---->`ConnectInterceptor`--->`CallServerInterceptor`(forWebSocket)



前置-->中置（调用下一个链并等待返回）-->后置-->返回

1. RetryAndFollowUpInterceptor：前置：初始化工作       后置：请求失败的时候重试，以及重定向的自动请求
2. `BridgeInterceptor`：前置：content-Length的计算添加、gzip的支持和压缩等        后置：移除content-Length、gzip的解包等
3. `CacheInterceptor`：负责Cache处理。前置：有可用缓存就用缓存            后置： 看是否要存到缓存中
4.  `ConnectInterceptor`：前置：最难最复杂的，建立连接（TCP或者TLS连接），并创建HttpCodec用于HTTP的编码解码
5. `CallServerInterceptor`负责实质的请求和响应的I/O操作，从Socket写入请求数据和从Socket读取响应数据





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
2. 从链接池里面找不支持多路复用的链接（HTTP1.1）
3. 从链接池里寻找可用的链接
4. 自己建立链接
5. 建立后再次寻找有无可用的多路复用链接（防止并发）



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
      if (connectionPool.callAcquirePooledConnection(address, call, null, false)) {
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
    var localRouteSelector = routeSelector
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
      if (connectionPool.callAcquirePooledConnection(address, call, routes, false)) {
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
      result = RealConnection(connectionPool, selectedRoute!!)
      connectingConnection = result
    }
  }

  // If we found a pooled connection on the 2nd time around, we're done.
  if (foundPooledConnection) {
    eventListener.connectionAcquired(call, result!!)
    return result!!
  }

  // Do TCP + TLS handshakes. This is a blocking operation.
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
  synchronized(connectionPool) {
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
      connectionPool.put(result!!)
      call.acquireConnectionNoEvents(result!!)
    }
  }
  socket?.closeQuietly()

  eventListener.connectionAcquired(call, result!!)
  return result!!
}
```



