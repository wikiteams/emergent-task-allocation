import csv

file_name = 'languages.yml'
how_many = 0

name = None
ltype = None


def write_to_csv(outputter, c, type):
    if (ltype is None):
        outputter.writerow([c,'unknown/other'])
    else:
        outputter.writerow([c,ltype.strip()])


with open('all-languages.csv', 'wb') as csvfile:
    outputter = csv.writer(csvfile, delimiter=',', quoting=csv.QUOTE_NONE)
    with open(file_name) as f:
        content = f.readlines()
        for c in content:
            if (c.startswith('# ')):
                continue
            if (len(c.strip()) == 0):
                if (name is not None):
                    write_to_csv(outputter, name, ltype)
                continue
            if (c.startswith('  type:')):
                ltype = str(c.split(':')[1])
            if (c.strip().endswith(':') is True) and (c.count('aliases') < 1) and (c.count('extensions') < 1) and (c.count('filenames') < 1):
                #write_to_csv(outputter, [str(c.strip().replace(':', ''))])
                name = str(c.strip().replace(':', ''))
                ltype = None
                how_many = how_many + 1
print how_many
csvfile.close()
