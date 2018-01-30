### group by

```sql
-- group by
select sid, SUM(CHOICES.score)
from CHOICES 
group by CHOICES.sid 
having SUM(CHOICES.score) > 400 
order by SUM(CHOICES.score) desc
--查询对于每一个学生而言的分数总和，
--将所有出现过的分数
--以学生的 ID 为标准分为多个类
```

### union

```sql
select sname, grade 
from STUDENTS 
where grade = "32"

union -- 另一个所在的年级

select sname, grade 
from STUDENTS 
where grade = 
	( select grade 
	  from STUDENTS 
	  where sid = '850955252')
```

### not in

```sql
select sid 
from STUDENTS -- 排除选择 Java 的学生 
where sid not in
	( select sid -- 选择 Java 
	from CHOICES, COURSES)
```

### all & not null

```sql
select * 
from COURSES 
where COURSES.hour <= -- 子查询， 小于每一个 
	all(
		select hour 
		from COURSES 
		where hour is not null
	)
```

### any

```sql
select cname 
from COURSES, CHOICES 
-- 教授 UML 课程的老师 ID 
where CHOICES.cid = COURSES.cid
	and CHOICES.tid = 
		ANY(
			select tid 
			from CHOICES 
			-- UML 课程 ID 
			where cid = 
			(
				select cid 
				from COURSES 
				where cname = 'uml'
			)
		)
```

### intersect

```sql
-- database 的查询 
select sid 
from CHOICES, COURSES 
where cname = 'database' 
	and CHOICES.cid = COURSES.cid 
	
intersect -- 巴啦啦能量！连接！ 
-- UML 的查询 
select sid 
from CHOICES, COURSES
```

### except

```sql
-- database 的查询 
select sid 
from CHOICES, COURSES 
where cname = 'database' 
	and CHOICES.cid = COURSES.cid 
	
except
-- UML 的查询 
select sid 
from CHOICES, COURSES 
where cname = 'uml' 
	and CHOICES.cid = COURSES.cid
```

### create view

```sql
create view viewc
		(no, sid, tid, cid, score, cname) 
as select cis.no, cis.sid, cis.tid, cis.cid, cis.score, crs.cname 
from CHOICES as cis, COURSES as crs 
where cis.cid = crs.cid
```

### 创建行列子集视图

```sql
create view s1 (sid, sname, grade) 
as select sid, sname, grade from STUDENTS 
where grade > 1998 
with check option
```

### 基于视图的更新

```sql
update views 
set score = score + 5 
where sname = 'uxjof'
```

### 视图的删除

```sql
drop view s1 
drop view viewc 
drop view views
```

### 用户授权

```sql
grant select 
on students 
to public
```

```sql
grant select, insert, update 
on courses 
to public
```

### 传递授权

```sql
grant select 
on teachers 
to user2 
with grant options
```

### 权限回收

```sql
revoke select 
on teachers 
from user1 , user2
```

### null

1. 在计算元组的算法中，null虽然不能表示明确的值，但是也是会被记为一条记录的。
2. 这里Null是存在的，在升序排列中，Null在最上面，被系统当 做最小的值处理。
3. 条件副词设置为any或者some，副词后的查询语句是可以给出一个精准的答案
4. all需要对所有的数据都进行 比较——这里如果有一个比较数据是Null，那么返回值就是Null

### primary key 创建表单

```sql
create table class (
    class_id varchar(4),‐‐ not null unique, 
    name varchar(10), 
    department varchar(20) 
    constraint hahaha primary key (class_id)
)
```

### 创建事务T3

```sql
set xact_abort on 
begin transaction t3 
insert into class 
values ('0001', '01csc', 'CS') 
begin transaction t4 
	insert class 
	values ('0001', '01csc', 'CS')
commit transaction t4
```

### 修改约束与插入删除操作

```sql
alter table sc 
	drop constraint FK__sc__cno__4F7CD00D
alter table sc 
	add constraint FK_sc_cno foreign key (cno) 
		references course(cno) on delete no action
alter table sc 
	drop constraint FK__sc__sno__4E88ABD4
alter table sc 
	add constraint FK_sc_sno foreign key (sno) 
		references stu_union(sno) on delete no action
```

设置当前的属性约束为on delete no action，因此在该项数据属性有数据存在时不允许删除。

### 修改键的约束为on delete set NULL

```sql
alter table sc 
	drop constraint FK_sc_cno
alter table sc 
	add constraint FK_sc_cno foreign key (cno) 
		references course(cno) on delete set null
alter table sc 
	drop constraint FK_sc_sno
alter table sc 
	add constraint FK_sc_sno foreign key (sno) 
		references stu_union(sno) on delete set null
```

由于一个或多个引用列不可为 Null，因此无法使用 SET NULL 引用操作创建外键 "FK_sc_cno"，这是因为我们在创建这关系的时候对于以上的变量sno和cno都设置为不可为NULL，这与当前的操作发生矛盾，故而不能修改。

### lab10

##### 为table添加约束 

##### 检验约束信息

##### 为table添加约束冲突 

##### 为table添加规则 

##### 检验规则信息

##### 基于约束添加规则

### lab11

#### 触发器建立与insert操作的限制 

#### 对Update操作建立触发器 

#### 对更新操作作出限制 

#### 建立视图与触发器insert函数

### lab12

##### 嵌套事务 

##### 事务保存点 

##### 事务储存

### lab13

#### 未提交读模式 

#### 提交读模式 

#### 可重复读隔离模式 

#### 幻象读

#### 可串行化隔离模式

### lab14

#### 锁争夺 

#### 查看阻塞 

#### 设置超时 

#### 演示死锁

### lab15

#### 创建用户信息 

#### 创建视图 

#### 授权查询 

#### 授权修改 

#### 查询日志















