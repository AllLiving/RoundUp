use School
set xact_abort on
begin transaction  tmp

1
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

insert into sc values ('95002', '0001', 2)
insert into sc values ('95002', '0002', 2)
insert into sc values ('10001', '0001', 2)
insert into sc values ('10001', '0002', 2)
��Ϣ 2627������ 14��״̬ 1���� 17 ��
Υ���� PRIMARY KEY Լ�� 'pk_sc'�������ڶ��� 'dbo.sc' �в����ظ�����


delete 
from stu_union
where sno = '10001'
��Ϣ 547������ 16��״̬ 0���� 1 ��
DELETE ����� REFERENCE Լ��"FK_sc_sno"��ͻ��
�ó�ͻ���������ݿ�"School"����"dbo.sc", column 'sno'��
 Because no action

select *
from sc

2
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
		��Ϣ 1761������ 16��״̬ 0���� 39 ��
����һ�����������в���Ϊ Null��
����޷�ʹ�� SET NULL ���ò���������� "FK_sc_cno"��

3
alter table icbc_card
	drop constraint FK__icbc_card__stu_c__5535A963
alter table icbc_card
	add constraint FK__icbc_card__stu_c foreign key (stu_card_id)
		references stu_card(card_id) on delete set null

delete
from STUDENTS
where sid='800001216'
��Ϣ 547������ 16��״̬ 0���� 58 ��
DELETE ����� REFERENCE Լ��"FK_CHOICES_STUDENTS"��ͻ��
�ó�ͻ���������ݿ�"School"����"dbo.CHOICES", column 'sid'��

4
create table listen_course(
	teacher_id char(6),
	tname varchar(20),
	course_id char(4),
	constraint pk_listen_course primary key(teacher_id)
	)
create table teach_course(
	course_id char(4),
	cname varchar(30),
	teacher_id char(6),
	constraint pk_teach_course primary key (course_id),
	constraint fk_teach_course foreign key (teacher_id)
								references listen_course(teacher_id)
	)

alter table listen_course
	add constraint fk_listen_course foreign key(course_id)
									references teach_course(course_id)

create table stu_help(
	stu_id char(6), 
	sname varchar(20),
	recipient char(6)
	constraint pk_stu_help primary key(stu_id)
	)
alter table stu_help
	add constraint fk_stu_help foreign key(recipient)
								references stu_help(stu_id)

5
create table manage(
	member_id char(6),
	manager_id char(6),
	constraint pk_manage primary key(member_id)
	)
create table supervis(
	viser_id char(6),
	vised_id char(6),
	constraint pk_supervis primary key(viser_id, vised_id)
	)
create table employer(
	leader_id char(6),
	follower_id char(6),
	supervis char (6),
	constraint pk_employer primary key(leader_id),
	constraint fk_employer_vis foreign key(supervis)
							references supervis(viser_id),
	constraint fk_employer_led foreign key(leader_id)
							references supervis(vised_id)
	)
create table employee(
	follower_id char(6),
	leader_id char(6),
	constraint pk_employee primary key(follower_id),
	constraint fk_employee_fllw foreign key(follower_id)
								references manage(member_id)
	constraint fk_employee_lead foreign key(leader_id)
								references manage(manager_id)
	)










commit transaction tmp