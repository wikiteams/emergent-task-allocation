from pymongo import MongoClient
import sqlite3

client = MongoClient('localhost', 27017)
database = client.wikiteams

collection = database.pullrequests
