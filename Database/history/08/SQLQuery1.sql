use School

--set XACT_abort on
--begin transaction T2
--insert Stu_Union
--values('95009', 'liyong', 'M', 25, 'EE');
--insert Stu_Union
--values('95003', 'wanghao', 'O', 25, 'EE')
--insert Stu_Union
--values('95005', 'wanghao', 'O', 25, 'EE')

--create table schp
--(
--	m_id varchar(10), stu_id char(10) not null unique, r_money int
--)

--set xact_abort on
--begin transaction tschp
--insert schp
--values('0001', '20000', 3000)
--insert schp
--values('0001',  '3000', 5000)
--**********************
--alter table schp
--add constraint PK_hahaha primary key (stu_id)

--select * 
--from schp


--1
--************************
--create table class
--(
--	class_id varchar(4),-- not null unique,
--	name varchar(10),
--	department varchar(20)
--	constraint hahaha primary key (class_id)
--)

--2
--set xact_abort on
--begin transaction t3
--	insert into class
--	values ('0001', '01csc', 'CS')
--	begin transaction t4
--		insert class
--		values ('0001', '01csc', 'CS')
		
		
		
		--default not null
	