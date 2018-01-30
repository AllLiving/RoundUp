--1
--create view viewc
--(no, sid, tid, cid, score, cname)
--as select cis.no, cis.sid, cis.tid,  cis.cid, cis.score, crs.cname
--from CHOICES as cis, COURSES as crs
--where cis.cid = crs.cid

--2
--create view views
--(no, sid, tid, cid, score, sname)
--as select
--CHOICES.no, choices.sid, choices.tid, choices.cid, choices.score, students.sname
--from CHOICES, STUDENTS
--where choices.sid = STUDENTS.sid

--3
--create view s1
--(sid, sname, grade)
--as select
--STUDENTS.sid, STUDENTS.sname, STUDENTS.grade
--from STUDENTS
--where grade > 1998
--with check option

--4
--select *
--from views
--where sname = 'uxjof'

--5
--select *
--from viewc
--where cname = 'uml'

--6
--insert
--into s1
--values(60000001, 'Lily', 2001)

--7
--insert
--into s1
--values(60000001, 'Lily', 1997)
--delete
--from STUDENTS
--where grade = 1999

--8
--update views
--set score = score + 5
--where sname = 'uxjof'
--select *
--from views
--where sname = 'uxjof'

--9
drop view s1
drop view viewc
drop view views