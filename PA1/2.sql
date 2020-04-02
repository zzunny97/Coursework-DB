select building from section group by building order by count(building) desc limit 1 offset 1;
