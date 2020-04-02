delimiter $$
create function letter_to_double(letter varchar(2)) returns float(2,1)
	deterministic
	begin
	declare ret float(2,1);
	if letter = 'A+' then
	set ret = 4.3;
	elseif letter = 'A' then
	set ret = 4.0;
	elseif letter = 'A-' then
	set ret = 3.7;
	elseif letter = 'B+' then
	set ret = 3.3;
	elseif letter = 'B' then
	set ret = 3.0;
	elseif letter = 'B-' then
	set ret = 2.7;
	elseif letter = 'C+' then
	set ret = 2.3;
	elseif letter = 'C' then
	set ret = 2.0;
	elseif letter = 'C-' then
	set ret = 1.7;
	elseif letter = 'D+' then
	set ret = 1.3;
	elseif letter = 'D' then
	set ret = 1.0;
	elseif letter = 'D-' then
	set ret = 0.7;
	elseif letter = 'F' or letter = NULL then
	set ret = 0;
	end if;
	return (ret);
	end
	$$

delimiter ;

create view takes_student as select ID, name, course_id, letter_to_double(grade) as grade from takes natural join student where dept_name = 'Comp. Sci.';
select ID, name, sum(grade * credits) / sum(credits) as average_GPA from takes_student natural join course group by ID;
