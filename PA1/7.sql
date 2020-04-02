delimiter $$
create trigger capacity_trigger 
before insert on takes 
for each row 
begin 
	if (select count(*) from takes natural join section where course_id = new.course_id and year = new.year and semester = new.semester and sec_id = new.sec_id) >= (select capacity from classroom natural join section where course_id = new.course_id and year = new.year) 
	then signal sqlstate '45000'; 
	end if; 
end;$$

delimiter ;
