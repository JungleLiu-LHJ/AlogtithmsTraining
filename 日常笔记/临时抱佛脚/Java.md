## Object方法

（一）Object()：默认构造方法

（二）clone()：创建并返回此对象的一个副本

（三）equals()：指示某个其他对象是否与此对象相等

（四）finalize()：当垃圾回收器确定不存在对该对象的更多引用时，由对象的垃圾回收器调用此方法

（五）getClass()：返回一个对象的运行时类

（六）hashCode()：返回该对象的哈希值

（七）notify()：唤醒此对象监视器上等待的单个线程

（八）notifyAll()：唤醒此对象监视器上等待的所有线程

（九）toString()：返回该对象的字符串表示

（十）wait()：导致当前的线程等待，直到其它线程调用此对象的notify()或notifyAll()

（十一）wait(long timeout)：导致当前的线程等待调用此对象的notify()或notifyAll()

（十二）wait(long timeout, int nanos)：导致当前的线程等待，直到其他线程调用此对象的notify()或notifyAll()，或其他某个线程中断当前线程，或者已超过某个实际时间量

（十三）registerNatives()：对本地方法进行注册

