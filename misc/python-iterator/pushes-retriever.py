from pymongo import MongoClient
import sys
import sqlite3

sqlconn = sqlite3.connect('socbase.db')

client = MongoClient('10.4.4.21', 27017)
database = client.wikiteams

collection = database.pushesleast


class Task:

    language = None
    workDone = None
    workLeft = None


for push in collection.find():
    sys.stdout.write('.')

    pushcreated = push.created_at
    repocreated = push.repository.created_at
    repourl = push.repository.url
    language = push.repository.language
    workunit = push.payload.size

    # reprezentuj repo.created_at jako liczba unixowa (bigdouble)

    # sortowalna kolekcja 

    # posortuj ja metoda pythonowa .sort() albo sorted()

    # a w osobnym slowniku trzymaj slownik, gdzie klucze to sa wlasnie
    # wspomniane daty jako unix standard double, a wartosc to obiekt klasy Task

print 'Done.'
