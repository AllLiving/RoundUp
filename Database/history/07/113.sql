--1
--create view CPP as
--select *
--from CHOICES
--where CHOICES.cid
--	=(select cid 
--	from COURSES
--	where COURSES.cname = 'c++')

--select COUNT(*)
--from CHOICES
--where cid = '10005'

--select COUNT(*) as wans
--from cpp 
--where score < 60

--select COUNT(*) as accept
--from cpp
--where score >= 60

--select COUNT(*) as nonexist
--from cpp
--where score is null

--2
--select sid, score
--from cpp
--order by score asc

--3
--select distinct sid, score
--from cpp
--where score is null
--order by score asc

--4
--select distinct grade
--from STUDENTS
--order by grade

--5
--select AVG(score) as average,
--	 COUNT(*) as cnt, 
--	 MAX(score) as maxone, 
--	 MIN(score) as minione
--from CHOICES
--group by cid

--6
use School

--select grade
--from STUDENTS as new
--where new.grade
--	>= all
--	(select grade
--	from STUDENTS as whole
--	where whole.grade is not null)

--select distinct sid, score
--from cpp
--where score is null
--order by score asc
	
--select grade
--from STUDENTS
--where grade is not null
--group by grade asc

--select distinct grade
--from STUDENTS
--order by grade asc


select grade
from STUDENTS as new
where new.grade
	>= null