# -*- coding: utf-8 -*-
import jft

fin = open("chichi.csv", "r")
fout = open("food.json", "w")

fout.write('[')
preb = ""
prer = ""
for line in fin.readlines():
    while line[-1] == '\n' or line[-1] == '\r':
        line = line[0:-1]
    line = line.replace('\\', '\\\\')
    arr = line.split(';')
    photographer = arr[0]
    name = arr[1]
    restaurant = arr[2]
    if (restaurant == '哈乐餐厅'):
        building = "哈乐"
    else:
        building = restaurant[0:6]
        restaurant = restaurant[6:]
    price = arr[3]
    description = arr[4]
    url = arr[5]
    taste = ""
    genre = ""

    if (preb == ''):
        fout.write('{'
                   + '"building":"' + building + '",'
                   + '"restaurants":[{'
                     '"restaurant":"' + restaurant + '",'
                   + '"foods":['
        )
    else:
        if (prer != restaurant):
            fout.write(']}')
            if (preb != building):
                fout.write(']},'
                           + '{'
                           + '"building":"' + building + '",'
                           + '"restaurants":['
                )
            else:
                fout.write(',')
            fout.write('{' +
                       '"restaurant":"' + restaurant + '",'
                       + '"foods":['
            )
        else:
            fout.write(',')

    fout.write('{'
               + '"name":"' + jft.j2f("utf8", "utf8", name) + '",'
               + '"url":"' + url + '",'
               + '"genre":"' + genre + '",'
               + '"price":"¥' + price + '",'
               + '"taste":"' + taste + '",'
               + '"description":"' + description + '"'
               + '}'
    )
    preb = building
    prer = restaurant
fout.write(']}]}]')
