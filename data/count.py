import csv
lang=set()
with open('tasks-skills.csv') as f:
    c = csv.reader(f, delimiter=',')
    c.next()
    for row in c:
        if row[2] not in lang:
            lang.add(row[2])

print len(lang)
print lang