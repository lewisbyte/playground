# 记录一次maven JVM 调优

## 前言

- 记录一次无（摸）聊（鱼）的GC 调优过程，本着学以致用、务实求真的精神，看了《深入理解Java虚拟机》中作者对自己的开发ide eclipse 工具做了`JVM` 调优，觉得很有意思。感觉这个确实是可以用自己的知识来提升开发效率，所以就有了这次的玄学调优之旅，文中如果有错误，希望能够指出，非常感谢。


## 优化背景
- 我本人目前有个工作上的项目，编译的时间大概需要30多秒，这次的目标就是从`JVM`的层面折腾、优化编译速度，那么就开始吧

### 编译时间基准线
0. 优化前的编译时间，先跑个基准时间作为参考:
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  35.901 s
[INFO] Finished at: 2020-12-14T13:37:31+08:00
[INFO] ------------------------------------------------------------------------
```

### 第一次尝试，配置相关JVM 参数
1. 调优前的准备，打印 `JVM` 运行时的配置，这里配置的参数可以打印 `JVM` 的相关运行时的 gc 详情，用来观察虚拟机的运行状态
- `XX:+PrintVMOptions`  打印外部的传入的vm参数
- `XX:+PrintGCDetails` 打印发生在垃圾收集的时候，gc详情日志
- `XX:+PrintCommandLineFlags` 这里可以打印出：隐式或者显示传递给虚拟机的参数输出。这里的隐式其实算是非用户传入的参数，算是jvm自己填充的参数，可以把具体的相关运行参数被打印出来。
- 本次`JVM`传入的参数： `export MAVEN_OPTS=" -XX:+PrintVMOptions -XX:+PrintGCDetails -XX:+PrintCommandLineFlags"`

2. 执行 `mvn clean` ，尝试将`JVM` 运行时的参数打印出
```
VM option '+PrintVMOptions'
VM option '+PrintCommandLineFlags'
VM option '+PrintGCDetails'
-XX:InitialHeapSize=268435456 -XX:MaxHeapSize=4294967296 -XX:+PrintCommandLineFlags -XX:+PrintGCDetails -XX:+PrintVMOptions -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
[GC (Allocation Failure) [PSYoungGen: 65536K->9718K(76288K)] 65536K->9734K(251392K), 0.0062610 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
.
.
.
.
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.431 s
[INFO] Finished at: 2020-12-14T13:59:18+08:00
[INFO] ------------------------------------------------------------------------
Heap
 PSYoungGen      total 76288K, used 75254K [0x000000076ab00000, 0x0000000770000000, 0x00000007c0000000)
  eden space 65536K, 100% used [0x000000076ab00000,0x000000076eb00000,0x000000076eb00000)
  from space 10752K, 90% used [0x000000076eb00000,0x000000076f47dbc8,0x000000076f580000)
  to   space 10752K, 0% used [0x000000076f580000,0x000000076f580000,0x0000000770000000)
 ParOldGen       total 175104K, used 16K [0x00000006c0000000, 0x00000006cab00000, 0x000000076ab00000)
  object space 175104K, 0% used [0x00000006c0000000,0x00000006c0004000,0x00000006cab00000)
 Metaspace       used 16058K, capacity 16450K, committed 16640K, reserved 1064960K
  class space    used 1931K, capacity 2025K, committed 2048K, reserved 1048576K
```
### 第一次内存分析

- 可以看到这里的默认堆大小是：76288K, 年轻代的大小为：74m ，老年代 171m。可以看到在mvn编译启动的时候便立即发生了一次minor gc（[PSYoungGen: 65536K->9718K(76288K)]），是初始化的堆太小了，将项目完整编译一遍，试试看堆会扩容到多大，执行 ` mvn clean install `。编译的结果如下：编译的耗时为38s，堆的大小从开始的` 76288K ` 扩容到 ` 1287168K `，所以第一个优化点就是将默认堆的大小调大，减少扩容堆带来的时间损耗，调大年轻代的大小为`1GB`。
- 最终的 `JVM` 参数调整为：

- 本次`JVM`传入的参数：` export MAVEN_OPTS="-XX:+PrintHeapAtGC -Xmn1024m  -Xms2048m -Xmx2048m -XX:+PrintVMOptions -XX:+PrintCommandLineFlags -XX:+PrintGCDetails" `
### 调整堆大小，进行第二次尝试
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  38.060 s
[INFO] Finished at: 2020-12-14T15:19:05+08:00
[INFO] ------------------------------------------------------------------------
Heap
 PSYoungGen      total 1287168K, used 389414K [0x000000076ab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 1209344K, 25% used [0x000000076ab00000,0x000000077db49bd8,0x00000007b4800000)
  from space 77824K, 99% used [0x00000007bb400000,0x00000007bffffdc8,0x00000007c0000000)
  to   space 94208K, 0% used [0x00000007b4800000,0x00000007b4800000,0x00000007ba400000)
 ParOldGen       total 290304K, used 110998K [0x00000006c0000000, 0x00000006d1b80000, 0x000000076ab00000)
  object space 290304K, 38% used [0x00000006c0000000,0x00000006c6c65960,0x00000006d1b80000)
 Metaspace       used 57894K, capacity 62322K, committed 62592K, reserved 1103872K
  class space    used 6382K, capacity 7172K, committed 7296K, reserved 1048576K
```

### 第二次内存分析
- 遗憾的发现，我们的时间不仅没有减少，反而还增加了。观察gc日志，我发现虽然没有了堆的扩容，但是却产生了很多的minor gc，还有少许metaspace的full gc，这里我就怀疑是不是年轻代的大小太大，导致的GC。于是我尝试将 `-Xmn1024m` 参数删除，进行第三次测试，将jvm参数调整为：

- 本次`JVM`传入的参数：：` export MAVEN_OPTS="-XX:+PrintHeapAtGC -Xms2048m -Xmx2048m -XX:+PrintVMOptions -XX:+PrintCommandLineFlags -XX:+PrintGCDetails" `


### 第三次尝试

```

VM option '+PrintVMOptions'
VM option '+PrintCommandLineFlags'
VM option '+PrintGCDetails'
-XX:InitialHeapSize=2147483648 -XX:MaxHeapSize=2147483648 -XX:+PrintCommandLineFlags -XX:+PrintGCDetails -XX:+PrintVMOptions -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
.
.
.
.
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  35.620 s
[INFO] Finished at: 2020-12-14T15:26:05+08:00
[INFO] ------------------------------------------------------------------------
Heap
 PSYoungGen      total 553472K, used 114468K [0x0000000795580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 424448K, 3% used [0x0000000795580000,0x0000000796247788,0x00000007af400000)
  from space 129024K, 78% used [0x00000007b8200000,0x00000007be501928,0x00000007c0000000)
  to   space 137216K, 0% used [0x00000007af400000,0x00000007af400000,0x00000007b7a00000)
 ParOldGen       total 1398272K, used 145292K [0x0000000740000000, 0x0000000795580000, 0x0000000795580000)
  object space 1398272K, 10% used [0x0000000740000000,0x0000000748de3158,0x0000000795580000)
 Metaspace       used 57842K, capacity 62194K, committed 62336K, reserved 1103872K
  class space    used 6383K, capacity 7172K, committed 7296K, reserved 1048576K
```

### 第三次日志分析
- 尝试移除掉了负优化之后（捂脸，大约 快了`3s`左右的时间。这里可以看到年轻代的大小有所调整，速度有所提升，大概在500MB。为了仔细的观察gc 的情况，不至于看着混乱，于是我将gc 的日志输入到一个文件中` -Xloggc:./gc.log `,下面贴出完整的gc 日志打印：

```
Java HotSpot(TM) 64-Bit Server VM (25.271-b09) for bsd-amd64 JRE (1.8.0_271-b09), built on Sep 16 2020 16:54:38 by "java_re" with gcc 4.2.1 Compatible Apple LLVM 10.0.0 (clang-1000.11.45.5)
Memory: 4k page, physical 16777216k(184620k free)

/proc/meminfo:

CommandLine flags: -XX:InitialHeapSize=2147483648 -XX:MaxHeapSize=2147483648 -XX:+PrintCommandLineFlags -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintVMOptions -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC 
3.140: [GC (Metadata GC Threshold) [PSYoungGen: 493352K->37959K(611840K)] 493352K->37991K(2010112K), 0.0324232 secs] [Times: user=0.16 sys=0.03, real=0.04 secs] 
3.173: [Full GC (Metadata GC Threshold) [PSYoungGen: 37959K->0K(611840K)] [ParOldGen: 32K->37023K(1398272K)] 37991K->37023K(2010112K), [Metaspace: 20506K->20506K(1069056K)], 0.0630022 secs] [Times: user=0.40 sys=0.03, real=0.06 secs] 
4.939: [GC (Metadata GC Threshold) [PSYoungGen: 265335K->17127K(611840K)] 302359K->54230K(2010112K), 0.0162070 secs] [Times: user=0.10 sys=0.01, real=0.02 secs] 
4.956: [Full GC (Metadata GC Threshold) [PSYoungGen: 17127K->0K(611840K)] [ParOldGen: 37103K->40811K(1398272K)] 54230K->40811K(2010112K), [Metaspace: 34039K->34039K(1081344K)], 0.0567554 secs] [Times: user=0.47 sys=0.01, real=0.06 secs] 
7.199: [GC (Allocation Failure) [PSYoungGen: 524800K->30799K(611840K)] 565611K->71618K(2010112K), 0.0206191 secs] [Times: user=0.09 sys=0.00, real=0.02 secs] 
10.369: [GC (Allocation Failure) [PSYoungGen: 555599K->46401K(611840K)] 596418K->87263K(2010112K), 0.0353684 secs] [Times: user=0.18 sys=0.05, real=0.04 secs] 
12.176: [GC (Allocation Failure) [PSYoungGen: 571201K->58712K(611840K)] 612063K->99582K(2010112K), 0.0341386 secs] [Times: user=0.18 sys=0.02, real=0.03 secs] 
13.351: [GC (Allocation Failure) [PSYoungGen: 583512K->66532K(591360K)] 624382K->123804K(1989632K), 0.0805149 secs] [Times: user=0.29 sys=0.09, real=0.08 secs] 
15.009: [GC (Metadata GC Threshold) [PSYoungGen: 447655K->76668K(601600K)] 504928K->133948K(1999872K), 0.0391952 secs] [Times: user=0.30 sys=0.03, real=0.04 secs] 
15.048: [Full GC (Metadata GC Threshold) [PSYoungGen: 76668K->0K(601600K)] [ParOldGen: 57280K->86590K(1398272K)] 133948K->86590K(1999872K), [Metaspace: 55127K->55067K(1101824K)], 0.2553197 secs] [Times: user=1.80 sys=0.02, real=0.26 secs] 
17.057: [GC (Allocation Failure) [PSYoungGen: 524800K->18698K(601088K)] 611390K->105296K(1999360K), 0.0201318 secs] [Times: user=0.07 sys=0.02, real=0.02 secs] 
18.214: [GC (Allocation Failure) [PSYoungGen: 523018K->22484K(601600K)] 609616K->109090K(1999872K), 0.0158255 secs] [Times: user=0.09 sys=0.01, real=0.01 secs] 
19.663: [GC (Allocation Failure) [PSYoungGen: 526804K->27010K(605184K)] 613410K->113625K(2003456K), 0.0122234 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
20.413: [GC (Allocation Failure) [PSYoungGen: 535938K->27130K(602624K)] 622553K->113753K(2000896K), 0.0092859 secs] [Times: user=0.07 sys=0.00, real=0.01 secs] 
22.083: [GC (Allocation Failure) [PSYoungGen: 536058K->35399K(611840K)] 622681K->122029K(2010112K), 0.0127960 secs] [Times: user=0.07 sys=0.00, real=0.01 secs] 
23.143: [GC (Allocation Failure) [PSYoungGen: 556103K->44218K(607744K)] 642733K->130857K(2006016K), 0.0121441 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
24.467: [GC (Allocation Failure) [PSYoungGen: 564922K->43628K(617984K)] 651561K->130274K(2016256K), 0.0150819 secs] [Times: user=0.12 sys=0.00, real=0.02 secs] 
25.041: [GC (Allocation Failure) [PSYoungGen: 577644K->50829K(614912K)] 664290K->137483K(2013184K), 0.0171980 secs] [Times: user=0.12 sys=0.00, real=0.02 secs] 
26.725: [GC (Allocation Failure) [PSYoungGen: 584845K->49607K(619520K)] 671499K->136269K(2017792K), 0.0143381 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
27.863: [GC (Allocation Failure) [PSYoungGen: 588743K->49783K(618496K)] 675405K->136453K(2016768K), 0.0204793 secs] [Times: user=0.14 sys=0.00, real=0.02 secs] 
29.283: [GC (Allocation Failure) [PSYoungGen: 588919K->78840K(596480K)] 675589K->171466K(1994752K), 0.0301864 secs] [Times: user=0.24 sys=0.00, real=0.03 secs] 
32.061: [GC (Allocation Failure) [PSYoungGen: 596472K->90602K(608256K)] 689098K->203223K(2006528K), 0.0488591 secs] [Times: user=0.31 sys=0.03, real=0.06 secs] 
33.521: [GC (Allocation Failure) [PSYoungGen: 608234K->90605K(547840K)] 720855K->211839K(1946112K), 0.0405590 secs] [Times: user=0.26 sys=0.01, real=0.04 secs] 
34.908: [GC (Allocation Failure) [PSYoungGen: 547821K->88780K(546304K)] 669055K->224085K(1944576K), 0.0416360 secs] [Times: user=0.35 sys=0.02, real=0.04 secs] 
Heap
 PSYoungGen      total 546304K, used 535026K [0x0000000795580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 457216K, 97% used [0x0000000795580000,0x00000007b0949748,0x00000007b1400000)
  from space 89088K, 99% used [0x00000007b1400000,0x00000007b6ab3298,0x00000007b6b00000)
  to   space 125952K, 0% used [0x00000007b8500000,0x00000007b8500000,0x00000007c0000000)
 ParOldGen       total 1398272K, used 135304K [0x0000000740000000, 0x0000000795580000, 0x0000000795580000)
  object space 1398272K, 9% used [0x0000000740000000,0x00000007484223f8,0x0000000795580000)
 Metaspace       used 57880K, capacity 62258K, committed 62592K, reserved 1103872K
  class space    used 6387K, capacity 7172K, committed 7296K, reserved 1048576K
```
- 从GC 日志可以看到 metaspace 的容量太小，导致几次full gc ，这里我们调整永久代 metaspace 的大小，老年代的使用率同样也不高，所以添加 `JVM` 参数：` -XX:MetaspaceSize=256m`，再来一次：

- 本次`JVM`传入的参数：`export MAVEN_OPTS="-XX:+UseParallelGC -XX:+UseParallelOldGC -Xloggc:./gc.log -Xmn1024m -XX:+PrintHeapAtGC -XX:MetaspaceSize=256m -Xms2048m -Xmx2048m -XX:+PrintVMOptions -XX:+PrintCommandLineFlags -XX:+PrintGCDetails" `

### 第四次尝试
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  36.261 s
[INFO] Finished at: 2020-12-14T16:16:55+08:00
[INFO] ------------------------------------------------------------------------
```
### 第四次分析
- 这次调整之后，虽然没有产生一次full gc ，但是却产生了很多次的minor gc，时间没有降低，可以确定了是由于年轻代的空间设置的比较大，对象的来回复制同样也是非常耗时的。于是确定的将 `-Xmn1024m`移除，使用默认的比例。考虑 `JVM` 有自适应大小内存策略，每次 GC 后会重新计算 Eden、From 和 To 区的大小。于是加上了 `-XX:-UseAdaptiveSizePolicy`做一次尝试，继续重跑。

- 本次`JVM`传入的参数：`export MAVEN_OPTS="-XX:+UseParallelGC -XX:+UseParallelOldGC -Xloggc:./gc.log -XX:+PrintHeapAtGC -XX:MetaspaceSize=256m -XX:-UseAdaptiveSizePolicy -Xms2048m -Xmx2048m -XX:+PrintVMOptions -XX:+PrintCommandLineFlags -XX:+PrintGCDetails" `

### 第五次尝试
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  34.900 s
[INFO] Finished at: 2020-12-14T16:40:07+08:00
[INFO] ------------------------------------------------------------------------
```
### 第五次分析：
- 一共产生了20次的minor GC，0次full gc。这次终于跑进了 34 秒大关。成功玄学的优化了1秒钟


## G1
### 试一试G1吧！

- 接下来我们来试试G1-GC的垃圾回收器表现的性能如何，将原本的 `JVM` GC参数删除，配置为G1的GC 回收：

- 本次`JVM`传入的参数：`export MAVEN_OPTS="-XX:+UseG1GC"`

```
.
.
.
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  43.946 s
[INFO] Finished at: 2020-12-14T23:41:30+08:00
[INFO] ------------------------------------------------------------------------
```
- 很遗憾，G1并不适用于这种偏向吞吐量任务型的作业，折腾之旅此就为止，谢谢观看。