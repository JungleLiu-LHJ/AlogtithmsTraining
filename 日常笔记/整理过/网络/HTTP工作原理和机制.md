# HTTP工作原理和机制



## HTTP

一种超级文本的传输协议

浏览器内核：渲染引擎，把网页渲染出来

> Hypertext Transfer Protocol，超⽂本传输协议，和 HTML (Hypertext Markup Language 超⽂本标记语⾔) ⼀起诞⽣，⽤于在⽹络上请求和传输 HTML 内容。

![image-20210401000148815](C:\Users\40515\AppData\Roaming\Typora\typora-user-images\image-20210401000148815.png)





### Cache机制

1. 强缓存：不会向服务器发送请求，直接从缓存中读取资源

   1. **Expires ：**response header里的过期时间，浏览器再次加载资源时，如果在这个过期时间内，则命中强缓存。
   2. **Cache-Control:**当值设为max-age=300时，则代表在这个请求正确返回时间（浏览器也会记录下来）的5分钟内再次加载资源，就会命中强缓存。

   > 区别：Expires 是http1.0的产物，Cache-Control是http1.1的产物
   > 两者同时存在的话，Cache-Control优先级高于Expires
   > Expires其实是过时的产物，现阶段它的存在只是一种兼容性的写法

2. 协商缓存：向服务器发送请求，服务器会根据这个请求的request header的一些参数来判断是否命中协商缓存，如果命中，则返回304状态码并带上新的response header通知浏览器从缓存中读取资源；

   1. **If-None-Match: ETag**

      Etag是上一次加载资源时，服务器返回的**response header**，是对该资源的一种**唯一标识**

      只要资源有变化，Etag就会重新生成

      浏览器在下一次加载资源向服务器发送请求时，会将上一次返回的Etag值放到**request header**里的**If-None-Match**里

      服务器接受到If-None-Match的值后，会拿来跟该资源文件的Etag值**做比较**，如果相同，则表示资源文件没有发生改变，命中协商缓存。

   2. **If-Modified-Since：Last-Modified**（秒）

      Last-Modified是该资源文件**最后一次更改时间**,服务器会在**response header**里返回

      同时浏览器会将这个值保存起来，下一次发送请求时，放到**request headr**里的**If-Modified-Since**里

      服务器在接收到后也会**做对比**，如果相同则命中协商缓存

   

   

   