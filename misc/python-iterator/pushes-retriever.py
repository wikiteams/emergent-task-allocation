from pymongo import MongoClient
import sys
import sqlite3
import dateutil.parser
import time
from blist import sortedset

sqlconn = sqlite3.connect('socbase.db')

sqlconn.execute("drop table if exists workers")
sqlconn.execute("create table %s (%s text, %s text, %s text, %s number, %s number)" % ("workers", "time", "taskid", "language", "workdone", "workleft"))

client = MongoClient('127.0.0.1')
database = client.wikiteams

collection = database.pushesleast
counter = 0
datesids = sortedset()
tasks = dict()


class Task:

    repourl = None
    language = None
    workDone = None
    workLeft = None

    def __init__(self, repourl, language, workDone):
        self.repourl = repourl
        self.language = language
        self.workDone = workDone
        self.workLeft = workDone

    def setRepoUrl(self, repourl):
        self.repourl = repourl

    def setLanguage(self, language):
        self.language = language

    def setWorkDone(self, workDone):
        self.workDone = workDone

    def setWorkLeft(self, workLeft):
        self.workLeft = workLeft

    def incWorkLeft(self, workLeft):
        self.workLeft += workLeft


for push in collection.find():
    sys.stdout.write('.')
    counter += 1

    pushcreated = push['created_at']
    repocreated = push['repository']['created_at']
    repourl = push['repository']['url']
    language = push['repository']['language']
    workunit = push['payload']['size']

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

    # print push

    if unixtime in tasks:
        coordinates = tasks[unixtime]  # datetime coordinates for the TARDIS :)
        # juz jest taka data
        # sprawdz czy juz znamy to repo
        if repourl in coordinates:
            # jest juz informacja o tym repo
            task = coordinates[repourl]
            task.incWorkLeft(workunit)
            coordinates[repourl] = task
            tasks[unixtime] = coordinates
        else:
            # te repo wystapilo pierwszy raz
            coordinates[repourl] = Task(repourl, language, workunit)
            tasks[unixtime] = coordinates
    else:
        tasks[unixtime] = dict()
        # pierwszy raz taka data
        # wiec i te repo tez jest pierwszy raz
        coordinates = tasks[unixtime]
        coordinates[repourl] = Task(repourl, language, workunit)
        tasks[unixtime] = coordinates

    # sys.exit()

    # a w osobnym slowniku trzymaj slownik, gdzie klucze to sa wlasnie
    # wspomniane daty jako unix standard double, a wartosc to obiekt klasy Task

print 'Done.'

# posortuj ja metoda pythonowa .sort() albo sorted()
# sortedates = datesids.sort()
# nie trzeba, datesids jest samosortowalny

# now dump to sqlite3
# and also dump to flat files
for dateid in datesids:
    for taskid in tasks[dateid].iterkeys():
        task = tasks[dateid][taskid]
        sqlconn.execute('INSERT INTO workers VALUES (?,?,?,?,?)', (dateid, taskid, task.language, task.workDone, task.workLeft))

sqlconn.commit()
