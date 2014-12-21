import simplejson as json


counter = 0


with open('fabpot.json') as data_file:
    data = json.load(data_file)
    if ("message" in data) and (data["message"].startswith("Not enough information for")):
        print data["message"]

with open('github-users-stats.json') as data_file:
    data = json.load(data_file)
    while(counter < 21):
        print data[counter]['login']
        counter = counter + 1

with open('kevinsawicki.json') as data_file:
    data = json.load(data_file)

    for language in data["usage"]["languages"]:
        print language["count"]
        print language["language"]
        print language["quantile"]
        print "----------"
