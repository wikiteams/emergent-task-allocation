from pymongo import MongoClient
import sys
import sqlite3
import dateutil.parser
import time

sqlconn = sqlite3.connect('socbase.db')

client = MongoClient('127.0.0.1')
database = client.wikiteams

collection = database.pushesleast

datesids = list()
tasks = dict()


class Task:

    repourl = None
    language = None
    workDone = None
    workLeft = None

    def setLanguage(self, language):
        self.language = language


for push in collection.find():
    sys.stdout.write('.')

    pushcreated = push['created_at']
    repocreated = push['repository']['created_at']
    repourl = push['repository']['url']
    language = push['repository']['language']
    workunit = push['payload']['size']

    repocreated = dateutil.parser.parse(repocreated)
    # reprezentuj repo.created_at jako liczba unixowa (POSIX)
    unixtime = time.mktime(repocreated.timetuple())

    # sortowalna kolekcja
    datesids.append(unixtime)

    print push

    if unixtime in tasks:
        coordinates = tasks[unixtime]  # datetime coordinates for the TARDIS :)
        # juz jest taka data
    else:
        tasks[unixtime] = set()
        # pierwszy raz taka data

    # sys.exit()

    # a w osobnym slowniku trzymaj slownik, gdzie klucze to sa wlasnie
    # wspomniane daty jako unix standard double, a wartosc to obiekt klasy Task

print 'Done.'

# posortuj ja metoda pythonowa .sort() albo sorted()
sortedates = datesids.sort()
