select SS.name from student as SS, section as S, takes as T where S.course_id = T.course_id and S.building = 'Painter' and S.year = 2009 and SS.ID = T.ID;
