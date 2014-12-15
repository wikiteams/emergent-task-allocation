select time, taskid, language, count(*)
from
(select time, taskid, language, sum(workdone), sum(workrequired) from workload group by taskid, language order by time asc)
group by taskid
HAVING 
    COUNT(*) > 1
order by taskid asc
 limit 100;
 
 
 
 select time, taskid, language, wd, wr
from
(select time, taskid, language, sum(workdone) as wd, sum(workrequired) as wr from workload group by taskid, language order by time asc)
order by taskid asc
 limit 100;
 
 
 select time, taskid, language, wd, wr, count(taskid) as counted
from
(select time, taskid, language, sum(workdone) as wd, sum(workrequired) as wr from workload group by taskid, language order by time asc)
group by taskid
HAVING ( counted > 1 )
order by taskid asc
 limit 100;
 
 
 ---------------------
 
 
 select taskid, count(taskid) as counted
from
(select time, taskid, language, sum(workdone) as wd, sum(workrequired) as wr from workload group by taskid, language order by time asc)
group by taskid
HAVING ( counted > 1 )
order by time asc
 limit 100;