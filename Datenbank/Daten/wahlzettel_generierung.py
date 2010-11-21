#!/usr/bin/python
# -*- coding: utf-8 -*-

import psycopg2

bundesland = 'Bayern'

try:
    conn = psycopg2.connect("dbname='Bundestagswahl'")
except:
    print "I am unable to connect to the database"

cur = conn.cursor(cursor_factory=psycopg2.extras.DictCursor)

# Finde alle Wahlkreise des Bundeslands
cur.execute("""SELECT Wahlkreis.nummer FROM Bundesland,Wahlkreis WHERE Bundesland='%s'""", % bundesland)
rows = cur.fetchall();
