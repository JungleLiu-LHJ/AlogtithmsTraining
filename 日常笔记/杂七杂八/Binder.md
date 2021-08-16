## Binder

> 全部摘自： https://zhuanlan.zhihu.com/p/35519585

 为什么需要Binder:

* 性能：Binder只需要一次数据拷贝，性能上仅此于共享内存
* 稳定性：基于C/S架构，职责明确、架构清晰，优于共享内存
* 安全性：为每个APP分配UID，可以用来鉴别进程身份



![preview](https://pic3.zhimg.com/v2-38e2ea1d22660b237e17d2a7f298f3d6_r.jpg)

>  操作系统的核心是内核，独立于普通的应用程序，可以访问受保护的内存空间，也可以访问底层硬件设备的权限。为了保护用户进程不能直接操作内核，保证内核的安全，操作系统从逻辑上将虚拟空间划分为用户空间（User Space）和内核空间（Kernel Space）。针对 Linux 操作系统而言，将最高的 1GB 字节供内核使用，称为内核空间；较低的 3GB 字节供各进程使用，称为用户空间。
>
> 内核空间（Kernel）是系统内核运行的空间，用户空间（User Space）是用户程序运行的空间。为了保证安全性，它们之间是隔离的。





**内存映射**

Binder IPC 机制中涉及到的内存映射通过 mmap() 来实现，mmap() 是操作系统中一种内存映射的方法。内存映射简单的讲就是将用户空间的一块内存区域映射到内核空间。映射关系建立后，用户对这块内存区域的修改可以直接反应到内核空间；反之内核空间对这段区域的修改也能直接反应到用户空间。



### Binder IPC 原理

![img](https://pic4.zhimg.com/80/v2-cbd7d2befbed12d4c8896f236df96dbf_720w.jpg)

1. 首先 Binder 驱动在内核空间创建一个数据接收缓存区；
2. 接着在内核空间开辟一块内核缓存区，建立**内核缓存区**和**内核中数据接收缓存区**之间的映射关系，以及**内核中数据接收缓存区**和**接收进程用户空间地址**的映射关系；
3. 发送方进程通过系统调用 copy*from*user() 将数据 copy 到内核中的**内核缓存区**，由于内核缓存区和接收进程的用户空间存在内存映射，因此也就相当于把数据发送到了接收进程的用户空间，这样便完成了一次进程间的通信。



### Binder 通信模型

Client、Server、ServiceManager、Binder 驱动这几个组件在通信过程中扮演的角色就如同互联网中服务器（Server）、客户端（Client）、DNS域名服务器（ServiceManager）以及路由器（Binder 驱动）之前的关系。

![preview](https://pic3.zhimg.com/v2-729b3444cd784d882215a24067893d0e_r.jpg)

> ServiceManager 和其他进程同样采用 Bidner 通信，ServiceManager 是 Server 端，有自己的 Binder 实体，其他进程都是 Client，需要通过这个 Binder 的引用来实现 Binder 的注册，查询和获取。ServiceManager 提供的 Binder 比较特殊，它没有名字也不需要注册。

ServiceManager有自己的binder实体（由Binder驱动创建，第一个binder），其他进程都需要通过这个binder的引用实现Binder的注册、查询和获取。一个app想向ServiceManager注册自己的Binder需要通过这个0号Binder和ServiceManager进行通信。



### Binder 通信过程

