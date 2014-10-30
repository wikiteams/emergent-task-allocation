from pymongo import MongoClient
import sys
import sqlite3
import dateutil.parser
import time
from blist import sortedset

sqlconn = sqlite3.connect('pullbase.db')

sqlconn.execute("drop table if exists forkers")
sqlconn.execute("create table %s (%s text, %s text, %s text, %s number, %s number)" % ("forkers", "time", "taskid", "language", "workdone", "workrequired"))

client = MongoClient('127.0.0.1')
database = client.wikiteams

collection = database.pullrequests
counter = 0
datesids = sortedset()
tasks = dict()


class Task:

    # repourl = None
    # the language bias is highly skewed which means that
    # language identifaction bearly changes during repo lifetime
    # yet, make it a list to persist many languages at once
    # socdata = dict()  # persisting (workdone, workleft)
    #workDone = None
    #workLeft = None

    def __init__(self, repourl, language, workDone):
        self.repourl = repourl
        self.socdata = dict()
        self.socdata[language] = {"workdone": workDone, "workleft": workDone+1}
        #self.workDone = workDone
        #self.workLeft = workDone

    def setRepoUrl(self, repourl):
        self.repourl = repourl

    def getSocData(self):
        return self.socdata

    def setWorkDone(self, language, workDone):
        self.socdata[language]["workdone"] = workDone

    def setWorkLeft(self, language, workLeft):
        self.socdata[language]["workleft"] = workLeft

    def incWorkLeft(self, language, workLeft):
        if language in self.socdata:
            self.socdata[language]["workleft"] += workLeft
        else:
            self.socdata[language] = {"workdone": workLeft, "workleft": workLeft+1}


for pull in collection.find():
    sys.stdout.write('.')
    counter += 1

    pushcreated = pull['created_at']
    repocreated = pull['repository']['created_at']
    repourl = pull['repository']['url']
    language = pull['repository']['language']
    workunit = pull['payload']['pull_request']['commits']

    repocreated = dateutil.parser.parse(repocreated)
    # reprezentuj repo.created_at jako liczba unixowa (POSIX)
    unixtime = time.mktime(repocreated.timetuple())

    if (counter % 1000 == 0):
        print ''
        print counter
        print ''
        #if counter is not 0:
        #    break

    # sortowalna kolekcja
    datesids.add(unixtime)

    # print pull

    if unixtime in tasks:  # tasks to dict gdzie klucze to posix
        coordinates = tasks[unixtime]  # datetime coordinates for the TARDIS :)
        # juz jest taka data
        # sprawdz czy juz znamy to repo
        if repourl in coordinates:
            # jest juz informacja o tym repo
            task = coordinates[repourl]
            task.incWorkLeft(language, workunit)
            #coordinates[repourl] = task
            #tasks[unixtime] = coordinates
        else:
            # te repo wystapilo pierwszy raz
            coordinates[repourl] = Task(repourl, language, workunit)
            #tasks[unixtime] = coordinates
    else:
        tasks[unixtime] = dict()
        # pierwszy raz taka data
        # wiec i te repo tez jest pierwszy raz
        coordinates = tasks[unixtime]
        coordinates[repourl] = Task(repourl, language, workunit)
        #tasks[unixtime] = coordinates

    # sys.exit()

    # a w osobnym slowniku trzymaj slownik, gdzie klucze to sa wlasnie
    # wspomniane daty jako unix standard double, a wartosc to obiekt klasy Task

print 'Done.'

# print datesids

# posortuj ja metoda pythonowa .sort() albo sorted()
# sortedates = datesids.sort()
# nie trzeba, datesids jest samosortowalny

# now dump to sqlite3
# and also dump to flat files
for dateid in datesids:
    #print 'in for dateid in datesids:' + str(dateid)
    for repourl in tasks[dateid].iterkeys():
        #print 'in for repourl in tasks[dateid].iterkeys():' + repourl
        task = tasks[dateid][repourl]
        for lang in task.getSocData().iterkeys():
            #print 'in for lang in task.getSocData().iterkeys():' + lang
            sqlconn.execute('INSERT INTO forkers VALUES (?,?,?,?,?)', (dateid, repourl, lang, task.getSocData()[lang]["workdone"], task.getSocData()[lang]["workleft"]))

sqlconn.commit()
