# HTTP工作原理和机制

Http:HyperText Transfer Protocol超文本传输协议



## HTTP

一种超级文本的传输协议

浏览器内核：渲染引擎，把网页渲染出来

> Hypertext Transfer Protocol，超⽂本传输协议，和 HTML (Hypertext Markup Language 超⽂本标记语⾔) ⼀起诞⽣，⽤于在⽹络上请求和传输 HTML 内容。

下面是如何把浏览器地址解析成请求的

<img src=".\image-20210401000148815.png" alt="image-20210401000148815" style="zoom:30%;" />



下面是一个请求的结构（注意这里是不规范的，get不应该有body）

<img src=".\image-20210812203047959.png" alt="image-20210812203047959" style="zoom: 50%;" />



这是一个响应的格式：

![image-20210812204643368](.\image-20210812204643368.png)



### 请求方法

* GET
  * 用来获取资源
  * 没有Body
* POST
  * 增加或修改资源(不具有幂等性)
  * 有Body
* PUT
  * 修改资源（具有幂等性）
  * 有Body
* DELETE
  * 删除资源
  * 没有Body
* HEAD
  * 和GET使用完全相同，但响应没有body
  * 用来获取信息：比如看是否支持断点续传，文件大小等



### Status Code 状态码 

三位数字，⽤于对响应结果做出类型化描述（如「获取成功」「内容未找到」）。

* 1xx：临时性消息。如：100 （继续发送）、101（正在切换协议）
* 2xx：成功。最典型的是 200（OK）、201（创建成功）。
* 3xx：重定向。如 301（永久移动）、302（暂时移动）、304（内容未改变）。
* 4xx：客户端错误。如 400（客户端请求错误）、401（认证失败）、403（被禁⽌）、404（找不到内容）。
* 5xx：服务器错误。如 500（服务器内部错误）。



### 一些header的作用

**Content-Length**：的作用：数据可能是文字也可能是二进制的，所以没办法选择一个标识符来作为结束，就用Content-Length作为长度

**Content-Type**:内容的类型。

1. text/html：html文本，用作浏览器页面
   1. 一般用get，@Query会放到url中
2. application/x-www-form-urlencoded:普通表单，encodedURL格式，用&符号划分文字
   1. retrofit用post，添加@FormUrlEncode，@Field
3. multipart/form-data:用于传二进制文件，需要添加一个比较长的分隔字符串，一般用来上传图片
   1. 一般用@part
4. aplication/json:json形式，用于Web Api的响应或POST/PUT请求
   1. @Body
5. 其他的：image/jpeg/application/zip..:单文件，用于Web Api响应或POST/PUT请求，直接传jpge图片

**Transfer-Encoding**:chunked,用于Chunked Transfer Encoding:分块传输编码，表示Body长度无法确定，Content-Length不能使用

，一般用在响应里面，数据量多，分批来下发。目的是在服务端还未获取到完整内容的时候，更快对客户端做出响应，减少用户等待

**Location**:重定向的目标URL，比如需要https

**User-Agent**:用户的内核版本或手机版本等

**Accept range/Range**:制定Body的内容范围。按范围取数据

​	Accept-Range: bytes 响应报⽂中出现，表示服务器⽀持按字节来取范围数据

​	Range: bytes=<start>-<end> 请求报⽂中出现，表示要取哪段数据

​	Content-Range:<start>-<end>/total 响应报⽂中出现，表示发送的是哪段数据作⽤：断点续传、多线程下载。

**Cookie/Set-Cookie**:发送Cookie/设置Cookie

**Authorization**:授权信息



下面是关于Cache的header	

**Last-Modified**;If-Modified-Since， 服务器会告诉你一个时间，从这个时间之前都不用来更新了

**Etag**:if-None-Match，服务器给一个标签，每次取得时候用这个标签给服务端请求一下看标签变了没

**Cache-Control**:它由一些能够允许你定义一个响应资源应该何时、如何被缓存以及缓存多长时间的指令组成

- **public**：表明响应可以被任何对象（包括：发送请求的客户端、代理服务器等等）缓存。

- **private**：表明响应只能被客户端缓存。

- **no-cache**：跳过强缓存，直接进入协商缓存阶段。

- **no-store**：表示当前请求资源禁用缓存

- **max-age=**：设置缓存存储的最大周期，超过这个时间缓存被认为过期（单位秒）

  







### Cache机制



#### 强缓存

不会向服务器发送请求，直接从缓存中读取资源

**Cache-Control**:它由一些能够允许你定义一个响应资源应该何时、如何被缓存以及缓存多长时间的指令组成

- **public**：表明响应可以被任何对象（包括：发送请求的客户端、代理服务器等等）缓存。
- **private**：表明响应只能被客户端缓存。
- **no-cache**：跳过强缓存，直接进入协商缓存阶段。
- **no-store**：表示当前请求资源禁用缓存
- **max-age=**：设置缓存存储的最大周期，超过这个时间缓存被认为过期（单位秒）,用这个来设置缓存时间

> 还有个Expires
>
> 区别：Expires 是http1.0的产物，Cache-Control是http1.1的产物
> 两者同时存在的话，Cache-Control优先级高于Expires
> Expires其实是过时的产物，现阶段它的存在只是一种兼容性的写法

#### 协商缓存

向服务器发送请求，服务器会根据这个请求的request header的一些参数来判断是否命中协商缓存，如果命中，则返回304状态码并带上新的response header通知浏览器从缓存中读取资源；

1. **If-None-Match: ETag（response）**

   Etag是上一次加载资源时，服务器返回的**response header**，是对该资源的一种**唯一标识**

   只要资源有变化，Etag就会重新生成

   浏览器在下一次加载资源向服务器发送请求时，会将上一次返回的Etag值放到**request header**里的**If-None-Match**里

   服务器接受到If-None-Match的值后，会拿来跟该资源文件的Etag值**做比较**，如果相同，则表示资源文件没有发生改变，命中协商缓存。

2. **If-Modified-Since：Last-Modified（response）**（秒）

   Last-Modified是该资源文件**最后一次更改时间**,服务器会在**response header**里返回

   同时浏览器会将这个值保存起来，下一次发送请求时，放到**request headr**里的**If-Modified-Since**里

   服务器在接收到后也会**做对比**，如果相同则命中协商缓存





## HTTPS

在客户端和服务端之间用非对称加密协商出一套对称密钥，每次发送信息之前将内容加密，收到之后解密。

在HTTP和TCP之间加了一个TLS层，用来加密



一共进行下面这几个步骤来建立连接：

1. C-->S：Client Hello  可供选择的TLS版本 + 可供选择的加密套件（对称，非对称，hash）+  随机数1
2. S-->C：Server Hello  选中的TLS版本 + 选中的加密套件 + 随机数2
3. S-->C：服务器证书 信任建⽴ 服务器公钥等信息 + 证书签发机构公钥等信息 + 根证书机构的公钥等信息
4. C-->S：Pre-master Secret  用服务端公钥加密的随机数3，双方根据随机数1,2,3生成对称加密的密钥（生成）
5. C-->S：客户端通知：将使⽤加密通信
6. C-->S： 客户端发送：Finished  将1~5步的消息做hash，并用对称加密加密后发给服务端，服务端也会做同样的操作来校验
7. S-->C：服务器通知：将使⽤加密通信
8. S-->C：服务器发送：Finished  将前面的消息做hash，并用对称加密加密后发给客户端，客户端也会做同样的操作来校验



#### 关于3. 服务器证书 信任建⽴

<img src=".\image-20210813001045128.png" alt="image-20210813001045128" style="zoom: 33%;" />

**服务器证书签名** = **证书签发机构的公钥** 对 **服务器证书公钥、主机名、地区等信息** 进行加密

其中根证书的信息是保存在本地的，本地对证书签发机构进行验证，获取到证书机构的公钥，再对服务器公钥进行验证



#### 关于4.Pre-master Secret  

根据之前的3个随机数客户端和服务端分别通过计算生成：

* 客户端加密密钥
* 服务端加密密钥
* 客户端MAC secret
* 服务端MAC secret



HMAC hash-based message authenticate code  通过hash来比对两个数据是否相同





