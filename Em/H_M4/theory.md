<center>

##嵌入式系统导论实验报告

</center>

-------

|  姓名  |    学号    |  班级  |     电话      |        邮箱         |
| :--: | :------: | :--: | :---------: | :---------------: |
| 曹广杰  | 15352015 | 1501 | 13727022190 | 1553118845@qq.com |

### 第16周

#### Question1

You wish to pass three numbers into a function, according to AAPCS how would you pass the numbers?

将三个参数传入寄存器R0， R1与R2中。

#### Question2

You wish to pass five numbers into a function, according to AAPCS how would you pass the numbers?

传递5个参数的时候，将数据传入寄存器R0，R1, R2, R3以及栈中。

#### Question3

What do each of the following assembly directives do? Answer each line separately, not as one complete program.

```asm
                              AA SPACE 10
                              BB RN 2
                              CC DCB 1,2,3
                              DD DCB "Jon\n\r",0
                              EE DCW 1,2,3
                              FF DCD 1,2,3
                              GG EQU 10
```

代码分析笔者使用注释表示：

```asm
;从第2行到第8行的命令，第一个参数都是自定义的。
;其后的语句用于表示对于该参数的操作。

AA	SPACE  10       ;为储存单元申请10个字节
BB	RN  2           ;指定label的数据为寄存器2的位置
CC	DCB 1,2,3       ;为CC分配bit，并使用其后的操作数为其赋值
DD	DCB "Jon\n\r",0 ;为DD分配bit，并使用字符为之赋值
EE	DCW 1,2,3       ;为EE分配半个字，使用其后的参数为其赋值
FF	DCD 1,2,3       ;为FF分配3个字节，并将其赋值为1，2，3
GG   EQU 10          ;定义GG为3
     EXPORT Fun      ;跳转到代码块FUN
     IMPORT Happiness ;连接代码块Happiness
     AREA    DATA, ALIGN=2 ;数据传入arm
	AREA |.text|,CODE,READONLY,ALIGN=2 ;将数据传入rom

```

指令解释：

1. SPACE：SPACE用于申请一片内存空间，与之类似的语句还有DCD，但是DCD申请空间之后会对其进行初始化。
2. RN：RN代表指定寄存器；
3. DCB：DCB分配一段字节的内存单元，其后的每个操作数都占有一个字节，操作数可以为-128～255的数值或字符串；
4. DCW：DCW分配一段半个字的内存单元，并使用其后的操作数为其赋值；
5. EXPORT：导出，跳转到；
6. IMPORT：导入，连接

#### Question4

Create an array in RAM that can hold ten 32-bit unsigned numbers called Buf. Write an assembly and a C function that sets the value of each element to its index. This function has no formal input or output parameters, but does modify the Buf array.

汇编语言操作如下：

```asm
      AREA DATA, ALIGN=2 
Buf   SPACE 40
      AREA |.text|,CODE,READONLY,ALIGN=2 
Set   LDR  R1,=Buf ;pointer
      MOV  R0,#0 ;n
Loop  STR  R0,[R1] ; BUF[n]=n
      ADD  R1,#1   ; next pointer
      ADD  R0,#1   ; n++
      CMP  R0,#10
      BLO  Loop
      BX   LR
```

C语言结构如下：

```c++
uint32_t Buf[10];
void Set(void){
  uint32_t n;
  for(n=0;n<10;n++){
    Buf[n] = n;
  }
}
```

#### Question5

How many bits wide is the SysTick timer?

系统时钟SysTick有24bit宽。

#### Question6

Does SysTick count up or down?

系统时钟SysTick采用下降沿触发计时。

#### Question7

Write a C function that uses SysTick to wait 100 μs. Assume the bus clock is running at 16 MHz.

c语言如下：

```c
void SysTick_100usWait(void){ 
  WAIT_TIME = 1599;     
  TMP = 0;       
  while((TMP&0x00010000)==0);
}
```

``
