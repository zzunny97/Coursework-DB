-- create view takes_student_all as select dept_name, ID, name, course_id, letter_to_double(grade) as grade from takes natural join student;

create view takes_student_all as select ID, name, dept_name, course_id, letter_to_double(grade) as grade from takes natural join student;

create view gpa_all as select dept_name, ID, name, sum(grade * credits) / sum(credits) as average_GPA from takes_student_all natural join course group by ID;

select dept_name, sum(average_GPA) / count(ID) as average_GPA from gpa_all group by dept_name order by average_GPA desc;
