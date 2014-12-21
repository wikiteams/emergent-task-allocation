import simplejson as json
import scream
import requests
import time
import csv


sleepy_head_time = 25
counter = 0
limit = 121


def freeze(message):
    global sleepy_head_time
    scream.log_warning('Sleeping for ' + str(sleepy_head_time) + ' seconds. Reason: ' + str(message), True)
    time.sleep(sleepy_head_time)

with open('results.csv', 'wb') as csv_file:
    csv_writer = csv.writer(csv_file, delimiter=';', quotechar='\"', quoting=csv.QUOTE_ALL)
    with open('github-users-stats.json') as data_file:
        data = json.load(data_file)
        while(counter < limit):
            developer_login = data[counter]['login']
            print "Starting to analyze OSRC card for user: " + str(developer_login)
            scream.progress_bar(counter, limit-1)

            tries = 5

            while True:
                try:
                    osrc_url = 'https://osrc.dfm.io/' + str(developer_login) + '.json'
                    scream.log_debug('The osrc url is: ' + osrc_url, True)
                    # OSRC was grumpy about the urllib2 even with headers attached
                    # hdr = {'User-Agent': 'Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7',
                    #        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
                    #        'Accept-Charset': 'ISO-8859-1,utf-8;q=0.7,*;q=0.3',
                    #        'Accept-Encoding': 'none',
                    #        'Accept-Language': 'en-US,en;q=0.8',
                    #        'Connection': 'keep-alive'}
                    # req = urllib2.Request(osrc_url, headers=hdr)
                    # response = urllib2.urlopen(req)
                    # thus i moved to requests library
                    proxy = {'http': '94.154.26.132:8090'}
                    session_osrc = requests.Session()
                    requests_osrc = session_osrc.get(osrc_url, proxies=proxy)
                    # print requests_osrc.text
                    osrc_data = json.loads(requests_osrc.text)
                    scream.say("JSON parsed..")
                    if ("message" in osrc_data) and (osrc_data["message"].startswith("Not enough information for")):
                        scream.say(osrc_data["message"])
                        limit = limit + 1
                        break
                    for language in osrc_data["usage"]["languages"]:
                        #print language["count"]
                        #print language["language"]
                        csv_writer.writerow([str(counter), str(developer_login), str(language["language"]), str(language["count"]), str(language["quantile"])])
                    scream.log_debug("Languages diagram for user " + str(developer_login) + ' created..', True)
                    # -----------------------------------------------------------------------
                    scream.log_debug('Finished analyze OSRC card for user: ' + str(developer_login), True)
                    break
                except Exception as e:
                    scream.log_error(str(e), True)
                    freeze('OSRC gave error, probably 404')
                    scream.say('try ' + str(tries) + ' more times')
                    tries -= 1
                finally:
                    if tries < 1:
                        developer_works_during_bd = 0
                        developer_works_period = 0
                        break
            #with open('progress_bar.lock') as f: scream.say(f.read())
            counter = counter + 1
