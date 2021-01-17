

# java 7 concurrentHashMap

* 采用分段（segment）锁的形式，段个数决定并发数
* 底层采用链表存储
* 多线程下，方法表现为非强一致性（isEmpty），只能保证最终一致性
* 多线程创建segment ，使用cas保证不会重复创建
* segment继承了reentrantlock
* 扩容时在锁的保护下进行，保证线程安全




# java 8 concurrentHashMap
* 相比较java7，java8的改进：不使用分段锁，而使用hashEntry来代替分段，锁的粒度更低；使用红黑树来优化查询；
*  



