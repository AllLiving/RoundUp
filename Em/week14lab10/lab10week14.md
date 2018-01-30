<center>

## 嵌入式系统导论实验报告

</center>

------

|  姓名  |    学号    |  班级  |     电话      |        邮箱         |
| :--: | :------: | :--: | :---------: | :---------------: |
| 曹广杰  | 15352015 | 1501 | 13727022190 | 1553118845@qq.com |

第14周，实验10——Performance debugging

### 计算优化时间

运行附件工程文件”Performance”，先选择不同优化级别，运行程序，观察elapsed 记录的对100和230400两个数求平方根的执行时间。

优化级别有4个选项，从0到3，在计算分别对100和230400进行开方的运算下得到的计算时间如下：

| 优化程度 | 被开方数100运算时间 | 被开方数230400运算时间 |
| :--: | :---------: | :------------: |
|  0   |     200     |      210       |
|  1   |     193     |      195       |
|  2   |     191     |      195       |
|  3   |     191     |      195       |

从以上优化情况对应的运行结果可以看出，对编译操作的适当优化可以缩短计算的时间。随着优化程度的提升，运算的时间也对应地缩短。

实验结果截图见附录。

### 观察PF1的波形

把注释语句对PF1开灯和关灯语句取代用定时器测量的elapsed语句，用逻辑分析仪观察 PF1波形，计算函数运算时间（仿真模式运行程序）。

把上文中使用的计时语句注释掉之后，换用对PF1的控制代码，得到实际操作如下：

```c++
GPIO_PORTF_DATA_R= 0x02;   // turn on led LED
tt = sqrt(ss);              
GPIO_PORTF_DATA_R = 0x00; // turn off led LED
```

在计算开方前后进行输出口的控制操作，此时得到的输出波形如下：

<img width="360" src="https://imgsa.baidu.com/forum/pic/item/173be5af2edda3ccac3b74a40ae93901203f9261.jpg" />

这是PF1的输出端口波形，该波形只有这一处方波信号，因为之前的代码在循环之外，运行之后就进入循环，故而这里只运行一遍。

### 查看内存记录

板级运行程序，观察pf2灯的亮灭、观察存入内存记录的PF2状态值，以及tt所求的ss 平方根值。

在实验板上运行代码，得到的运行结果如下：

PF2的输出情况：

<img width="380" src="https://imgsa.baidu.com/forum/pic/item/564bbb1fbe096b636a7390d407338744eaf8acde.jpg" />

这种输出是因为在while循环中的语句：

```c++
	while(1){
		Led = GPIO_PORTF_DATA_R;   // read previous
		Led = Led^0x04;            // toggle red LED
		GPIO_PORTF_DATA_R = Led;   // output GPIO_PORTF_DATA_R; 
		/**/
		SysTick_Wait10ms(100);                     // wait 1s
	}
```

这里的LED对应着输出的端口，随着循环的每一次运行，都对`0x04`进行异或运算，于是每次循环端口2都会与上次不同，由是得出矩形方波。由矩形方波的周期可以看出周期为2s，但是在循环的结尾部分添加了一个等待函数，恰好为1s，这说明用于运算的时间几乎为0。

内存记录的PF2状态值：

<img width="480" src="https://imgsa.baidu.com/forum/pic/item/ef14a2c4b74543a9d694344815178a82b80114b0.jpg" />

对于PF2的计算过程如下：

```c++
while(1){
  if(i<50){
    Data[i] = GPIO_PORTF_DATA_R&0x04; // record PF2
  }
}
```

在前50次循环内，每一次的运算都对Data序列进行异或运算，所以Data的变化趋势应该是非常明显，是典型的矩形方波。而数值变化也是在`0x00`与`0x04`之间波动。 

以及开方结果如下：

<img width="480" src="https://imgsa.baidu.com/forum/pic/item/ef14a2c4b74543a9d6cd344815178a82b8011469.jpg" />

开方运算的结果显示，在前31次运算中，始终有对于开方的运算结果，此后便不再变化，这是因为本次实验中的寄存器是32位的，在前31运算中，已经将32位填满，继续运算的过程中，开方之后的结果已经超出了32位寄存的范围，于是对于溢出操作，开方结果显示不再变化。

### 代码分析

本次实验的代码主要用于计算开方。

#### 端口的设置

```c++
  SYSCTL_RCGCGPIO_R |= 0x20;       // activate port F
  while((SYSCTL_PRGPIO_R&0x20)==0){};
  GPIO_PORTF_DIR_R |= 0x0E;        // make PF3-1 output (PF3-1 built-in LEDs)
  GPIO_PORTF_AFSEL_R &= ~0x0E;     // disable alt funct on PF3-1
  GPIO_PORTF_DEN_R |= 0x0E;        // enable digital I/O on PF3-1
```

步骤如下：

```flow
act=>start: 激活F端口
wat=>condition: 等待激活成功
sop=>operation: 关闭模拟功能，
开启数字功能

act(right)->wat
wat(yes, right)->sop(left)
wat(no, left)->wat
```

接下来，使用开方运算，并将输出的变化添加到开方的前后：

```c++
    GPIO_PORTF_DATA_R= 0x02;   // turn on led LED
    tt = sqrt(ss);              
	GPIO_PORTF_DATA_R = 0x00; // turn off led LED
```

在debug的时候，可以通过输出端口的波形变化确定运算的时间与周期。

此后进入死循环：

```c++
while(1){
  /*更新端口2的输出值*/
  tt = sqrt(ss);
  if(i<50){
    /*使用数组记录输出值变化*/
    i++;
    ss=ss*2;
  }
  // wait 1s
}
```

在这里使用寄存器对迭代相乘的数字开方运算进行记录，并将该操作循环50次。由此可以得出寄存器寄存信息的上限。

### 附录

实验一编译器优化的图片：

对100开方，优化程度为0的实验结果：

<img width="480" src="https://imgsa.baidu.com/forum/pic/item/a1f7e651f3deb48f6b67685afb1f3a292df57832.jpg" />

对100开方，优化程度为3的实验结果：

<img width="480" src="https://imgsa.baidu.com/forum/pic/item/94989843ad4bd113301c79b651afa40f4afb056a.jpg" />

对230400开方，优化程度为0的实验结果：

<img width="480" src="https://imgsa.baidu.com/forum/pic/item/393545d7912397dd615151805282b2b7d0a2871c.jpg" />

对230400开方，优化程度为3的实验结果：

<img width="480" src="https://imgsa.baidu.com/forum/pic/item/bac032899e510fb33432be13d233c895d1430c1c.jpg" />