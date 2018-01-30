--set xact_abort on
--begin transaction tmp

--go
--create trigger T5 on worker
--for update
--as
--	if(
--		(select sage
--		from inserted)
--		<
--		(select sage
--		from deleted)
--	)
--	begin
--		print 'Why much younger!?'
--		Rollback transaction
--	End
--commit transaction tmp

set xact_abort on
begin transaction tmp

update worker
set sage = 7
where number = '0001'

select *
from worker

commit transaction tmp