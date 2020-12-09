# collection 体系
### 介绍

* AbstractCollection 集合类，使用了模板模式，将重复的模版代码抽象到上层，避免重复的编写

* 抽象接口体系：List、Map、Array、Set（TreeSet,SortedSet）
* compareable 接口实现，需要注意对象判断不要使用减法或者亦或运算，会有溢出的风险，按照约定写成返回（-1，1，0）
* hashcode 和 equals 必须要同时重写


* array 考点归纳：
* ArrayList 基于什么实现的？数组
* 扩容是如何实现的？将数组扩容到原本的1.5 倍，将原有的数据拷贝到新的数组上面去

*  linkedlist 双链表实现
* 


* HashMap（基本组成：hash表+链表实现）
* 1.(扩容的)负载因子：loadfactor：0.75
* 2.为什么容量必须是2的幂？：


* TreeMap 
* 红黑树实现：需要了解树的基本性质


