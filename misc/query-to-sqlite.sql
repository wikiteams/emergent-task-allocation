select time, taskid, language, sum(workdone), sum(workrequired) from workload where taskid in 
(select taskid
from
(select time, taskid, language, sum(workdone) as wd, sum(workrequired) as wr from workload group by taskid, language order by time asc)
group by taskid
HAVING ( count(taskid) > 1 )
order by time asc) 
group by taskid, language order by time asc limit 100;