python datenbank.py
echo "Resetting schema. (Enter to start)"
read
psql -d Bundestagswahl -f ../schema.sql

echo "Adding bundesland. (Enter to start)"
read
psql -d Bundestagswahl -f bundesland.sql

echo "Adding partei. (Enter to start)"
read
psql -d Bundestagswahl -f partei.sql 

echo "Adding wahlkreis. (Enter to start)"
read
psql -d Bundestagswahl -f wahlkreis.sql

echo "Adding strukturdaten. (Enter to start)"
read
psql -d Bundestagswahl -f struktur.sql

#echo "Adding wahlbezirk. (Enter to start)"
#read
#psql -d Bundestagswahl -f wahlbezirk.sql

echo "Adding landesliste. (Enter to start)"
read
psql -d Bundestagswahl -f landesliste.sql

echo "Adding kandidat. (Enter to start)"
read
psql -d Bundestagswahl -f kandidat.sql

echo "Adding kandidat_wahlkreis. (Enter to start)"
read
psql -d Bundestagswahl -f kandidat_wahlkreis.sql

echo "Adding landesliste_kandidat. (Enter to start)"
read
psql -d Bundestagswahl -f landesliste_kandidat.sql

echo "Adding wahlergebnis. (Enter to start)"
read
psql -d Bundestagswahl -f wahlergebnis.sql

echo "Adding direktergebnis. (Enter to start)"
read
psql -d Bundestagswahl -f direktergebnis.sql

echo "Adding listenergebnis. (Enter to start)"
read
psql -d Bundestagswahl -f listenergebnis.sql

echo "Adding divisor. (Enter to start)"
read
psql -d Bundestagswahl -f divisor.sql