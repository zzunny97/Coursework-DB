--select name from instructor where ID in (select ID from teaches natural join course where course_id in (select prereq_id from prereq where course_id in (select course_id from student as S natural join takes as T where S.name = 'Williams' and T.year = 2009)));

create view tmp as select ID, title from teaches natural join course where course_id in (select prereq_id from prereq where course_id in (select course_id from student as S natural join takes as T where S.name = 'Williams' and T.year = 2009));
select name, title  from tmp natural join instructor;


