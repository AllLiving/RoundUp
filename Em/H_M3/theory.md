<center>

##嵌入式系统导论实验报告

</center>

-------

|  姓名  |    学号    |  班级  |     电话      |        邮箱         |
| :--: | :------: | :--: | :---------: | :---------------: |
| 曹广杰  | 15352015 | 1501 | 13727022190 | 1553118845@qq.com |

### 第14周

#### Question1

After an addition of two unsigned numbers, the C bit is set. What does it mean?

表示条件的标志码：

| Bit  |   Name   | 加减之后表示的意义 |
| :--: | :------: | :-------: |
|  N   | negative |   结果是负数   |
|  Z   |   zero   |   结果是0    |
|  V   | overflow | 有符号数字计算溢出 |
|  C   |  carry   | 无符号位计算溢出  |

所以在这里，两个无符号数字做和，此时C标记码被置为1，意为当前的做和操作出现溢出情况，超出32位。

#### Question2

After an addition of two signed numbers, the V bit is set. What does it mean?

有上文得知，V位被置意为当前的做和操作已经溢出——是有符号数字做和溢出。

#### Question3

After a subtraction of two unsigned numbers, the C bit is set. What does it mean?

两个无符号数字进行减法运算，如果C位置为0，说明当前的减法的计算结果溢。

#### Question4

After a subtraction of two signed numbers, the V bit is set. What does it mean?

在有符号位数字计算之后，如果当前的计算结果32位结构不能容纳，则V置为1，意为下溢。

#### Question5

Assume there are two 32-bit variables in RAM memory called In and Out. Write C code that sets Out equal to In plus 1.

C语言实现“输出 = 输入 + 1”，比较直观：

```ASM
								OUT = IN +1;
```

#### Question6

Assume there are two 32-bit variables in RAM memory called In and Out. Write assembly code that sets Out equal to In plus 1.

汇编语言实现`OUT = IN + 1`关系如下：

```asm
LDR R0, =IN  ;将IN在内存中的地址载入寄存器R0中
LDR R1, [R0] ;将R0寄存器中的地址在内存中对应的值赋值给寄存器R1
ADD R1, R1, 1
LDR R2, =OUT ;将OUT在内存中的地址载入到寄存器R2中
STR R1, [R2] ;将寄存器R1中的数字传输到R2对应的内存中去
```