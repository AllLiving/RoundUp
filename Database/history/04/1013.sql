use School
--1
--insert 
--into STUDENTS
--values('800022222', 'WangLan', null, null)

--2
--insert
--into TEACHERS
--values('200001000','LXL','s4zrck@pew,net','3024')

--3
--update TEACHERS
--set salary = 4000
--where tid = '200010493'

--4
--update TEACHERS
--set salary = 2500
--where salary < 2500

--5
--update CHOICES
--set tid = 
--	(select tid
--	from TEACHERS
--	where tname = 'rnupx')
--where tid = '200016731'

--6
--update STUDENTS
--set grade = '2001'
--where sid = '800071780'

--7
--delete
--from COURSES
--where COURSES.cid not in 
--	(select cid
--	from CHOICES)

--8
--delete
--from CHOICES
--where CHOICES.sid =
--	any(select sid
--	from STUDENTS
--	where grade < 1998)
--delete
--from STUDENTS
--where grade < 1998

--9
--select * 
--from STUDENTS
--where students.sid not in
--	(select sid
--	from CHOICES)

--10
--delete
--from CHOICES
--where score < 60