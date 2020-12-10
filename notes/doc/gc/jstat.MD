` jstat -gcutil pid 时间间隔/ms 重复次数 `

- jstat -gcutil 67730 1000 100

```
S0: 新生代中Survivor space 0区已使用空间的百分比
S1: 新生代中Survivor space 1区已使用空间的百分比
E: 新生代已使用空间的百分比
O: 老年代已使用空间的百分比
P: 永久带已使用空间的百分比
 

YGC: 从应用程序启动到当前，发生Yang GC 的次数
YGCT: 从应用程序启动到当前，Yang GC所用的时间【单位秒】
FGC: 从应用程序启动到当前，发生Full GC的次数
FGCT: 从应用程序启动到当前，Full GC所用的时间
GCT: 从应用程序启动到当前，用于垃圾回收的总时间【单位秒】
```