-- #1
select building from section group by building order by count(building) desc limit 1;
-- #2
select building from section group by building order by count(building) desc limit 1 offset 1;
-- #3
create view student_advisor as select student.ID as s_ID, student.dept_name as s_dept_name, advisor.i_ID as a_ID  from student natural join advisor where student.ID = advisor.s_ID; 
select instructor.ID as ID, instructor.name as name, student_advisor.s_dept_name as dept_name, count(student_advisor.s_ID) from instructor left outer join student_advisor on instructor.ID = student_advisor.a_ID group by instructor.ID;
-- #4
select SS.name from student as SS, section as S, takes as T where S.course_id = T.course_id and S.building = 'Painter' and S.year = 2009 and SS.ID = T.ID;
-- #5
create view tmp as select ID, title from teaches natural join course where course_id in (select prereq_id from prereq where course_id in (select course_id from student as S natural join takes as T where S.name = 'Williams' and T.year = 2009));
select name, title  from tmp natural join instructor;

-- #6
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
-- #7
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
-- #8
delimiter $$
create trigger advisor_trigger 
after insert on student 
for each row 
begin 
	insert into advisor(S_ID, i_ID) values (new.ID, (select ID from instructor where salary >= all (select salary from instructor where dept_name = new.dept_name) and dept_name = new.dept_name));  
END$$

delimiter ;

-- #9
-- this query is about which department gives better grades to students
create view takes_student_all as select ID, name, dept_name, course_id, letter_to_double(grade) as grade from takes natural join student;

create view gpa_all as select dept_name, ID, name, sum(grade * credits) / sum(credits) as average_GPA from takes_student_all natural join course group by ID;

select dept_name, sum(average_GPA) / count(ID) as average_GPA from gpa_all group by dept_name order by average_GPA desc;
