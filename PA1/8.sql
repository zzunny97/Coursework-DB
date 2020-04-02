delimiter $$
create trigger advisor_trigger 
after insert on student 
for each row 
begin 
	insert into advisor(S_ID, i_ID) values (new.ID, (select ID from instructor where salary >= all (select salary from instructor where dept_name = new.dept_name) and dept_name = new.dept_name));  
END$$

delimiter ;
