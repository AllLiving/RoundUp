--select *
--from CHOICES
--where score < 60

--revoke
--select 
--on courses
--from public

--delete
--from CHOICES
--where sid=500015294

--revoke
--select 
--on courses
--from user1

--1
--grant select
--on students
--to public

--2
--grant select, insert, update
--on courses
--to public

--3
--grant select
--on teachers
--to user1
--with grant option

--create view tchslry--
--as select salary
--from TEACHERS

--grant update
--on tchslry
--to user1
--with grant option

--4
--grant select
--on choices
--to user2

--create view sc
--as
--	select score
--	from CHOICES

--grant update
--on sc
--to user2

--5--6
--grant select
--on teachers
--to user2
--with grant option

--7
--revoke select
--on teachers
--from user1

revoke select
on courses
from user1, user2