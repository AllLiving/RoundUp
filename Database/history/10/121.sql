use School
set xact_abort on
begin transaction tmp

--1
--alter table worker
--drop constraint rule_age
--alter table worker
--add constraint rule_age check (sage >= 0)
--alter table worker
--add constraint U8 check (sage < 0)
--go
--create rule U3
--as @value>=0
--go
--exec sp_bindrule U3, 'worker.[sage]'

--exec sp_unbindrule 'worker.[name]'

--2 & 3
--insert into worker
--values('00004', 'ZONE', 'm', '-1', 'magic')

--insert into worker
--values('00005', 'Flash', 'm', '23', 'trans')

--select *
--from worker


--delete 
--from worker
--where number = '00005'

--4
--go
--create rule U4
--as @value < 0
--go
--exec sp_bindrule U4, 'worker.[sage]'

--5
--drop rule R2

--go
--create rule R2
--as @value in (0, 100)
--go
--exec sp_bindrule R2, 'worker.[sage]'

--6 & 7
--exec sp_unbindrule 'worker.[sage]'

--insert into worker
--values('00006', 'Taiyi', 'm', '1000', 'magic')

--8
go
create rule R3
as  @value > 50
go
exec sp_bindrule R3, 'worker.[sage]'

--delete 
--from worker 
--where number = '00003'
--select *
--from worker

--exec sp_bindrule U3, 'worker.[sage]'

--alter table worker
--drop rule rule_age

--go
--create rule rule_age23
--as @value > 0
--go
--exec sp_bindrule rule_age23, 'worker.[sage]';

--drop rule rule_age
--exec sp_unbindrule 'worker.[sage]'


commit transaction tmp