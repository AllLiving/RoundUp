<center>

### 嵌入式系统理论作业

</center>

<center> 曹广杰 

15352015 数据科学与计算机

授课教师：黄凯

2017/10/27</center>

#### 前提. Resource sharing

- 有一些资源不能被多个进程共同使用。
- 这种资源在使用的时候需要使用信号量建立临界区。
- 随着信号量的阻塞，当前申请信号量的线程进入block状态

#### Ex3.1

---

##### 1.

Explain the difference between private, shared and exclusive resources and give an example of problems encountered in exclusive resources.

---

private resource：只能由一个进程或者线程独自使用，是该线程的专属资源。

shared resource：可以被至少2个资源使用，由多个任务分享的资源。

exclusive resource：允许被多个线程使用，但是不允许同时使用，每次只有一个线程或者进程使用它。

***

##### 2.

Propose three ways such that the problem occurring in Resource Sharing Problem of mutual exclusion (mutual exclusion) can be solved

---

在多线程使用互斥资源（无信号量作用）的时候，可能会出现一些问题，有三种方式可以解决这类问题。

- 非抢占任务(Non-preemptive tasks)
- 禁止打断(interrupts disabled)
- 静态调度(static scheduling)
- 使用信号量资源(semaphores)

---

Explain the application of a possible solution of the previous part.

---

关于信号量的解释：

二元信号量就是可以实现**构建临界区，实现原子操作**的数据结构。

设若有一个二元信号量s，则其具有两种状态T/F。当有一个线程A申请使用临界区的时候，就申请这个信号量：

- 如果s是闲置的，则A进入临界区，使用结束之后释放s
- 如果s是正在被使用的，则A等待使用s的线程释放该资源



#### Ex 3.2 - Priority Inversion

---

##### 1.

In the lecture you learned about the Priority Inheritance Protocol (PIP). Does the PIP solve the problem of deadlocks? Give a brief explanation for your response.

---

PIP并没有解决死锁问题。

PIP表示线程之间的优先级继承（Priority Inheritance Protocol）：

当一个优先级较低的线程因为占有信号量而阻塞了优先级更高的线程，则该线程暂时获得被阻塞线程中的最高优先级。

这种设计的初衷是为了防止优先级反转，尽可能地保证了优先级高的线程在一定要比优先级较低的线程先运行。否则由于高优先级的抢占，信号量可能会被较低的优先级持续占有，而该进程又不断被抢占，使得高优先级的线程被无限阻塞。

**死锁情况**：

如果当前的较低优先级的线程申请一个已经被另一个线程（线程B）占有的资源，而B又申请该线程的资源就会造成死锁——PIP并没有针对这个问题做任何的处理，不能解决死锁的问题。

---

##### 2.

Given are the four tasks J1, J2, J3 and J4. The table below contains information on their arrival times, deadlines, their execution time and priorities. The tasks with fixed priorities should be executed by a priority-based scheduling processor that can tackle the tasks within their deadlines.

The drawings in the last row of the table provide information about the sequence of individual tasks. Each task contains one or more critical sections, in which the system accesses the two exclusive resources A and B, respectively. In the drawings, each block has one unit of length and non-critical sections are shaded. The critical sections are marked with the corresponding letters A and B. For example, the seven time units long task J4 contains two critical sections, the five time periods long section A and the three time units long section B. A and B are nested here, i.e. during the periods 3-5 J4 must have access to both resource A and resource B.

---

- Now create a scheduling with the Priority Inheritance Protocol and fill out the chart prepared below

<img src="https://imgsa.baidu.com/forum/pic/item/100527061d950a7b4161548901d162d9f2d3c930.jpg" />

- Are all deadlines satisfied through the application of the PIP? If not, how big is the maximum delay (maximum lateness) in units of time

Yes.

- Is there a schedule for the tasks specified J1 to J4 that meets all deadlines? What changes should be made to the priority? If there is a correct schedule, please show it in the graph provided below. Otherwise, explain why there can be no correct schedule.

如果通过修改优先级来实现所有的任务进程都可以在制定的deadline中完成，是可以实现的：

<img src="https://imgsa.baidu.com/forum/pic/item/006d7da85edf8db172497f290223dd54564e7421.jpg" />