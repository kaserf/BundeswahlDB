:: python datenbank.py
echo "Resetting schema."
:: read
psql -U postgres -d Bundestagswahl -f ../schema.sql

echo "Adding bundesland."
:: read
psql -U postgres -d Bundestagswahl -f bundesland.sql

echo "Adding partei."
:: read
psql -U postgres -d Bundestagswahl -f partei.sql 

echo "Adding wahlkreis."
psql -U postgres -d Bundestagswahl -f wahlkreis.sql

echo "Adding strukturdaten."
psql -U postgres -d Bundestagswahl -f struktur.sql

::echo "Adding wahlbezirk."
::psql -U postgres -d Bundestagswahl -f wahlbezirk.sql

echo "Adding landesliste."
psql -U postgres -d Bundestagswahl -f landesliste.sql

echo "Adding kandidat."
psql -U postgres -d Bundestagswahl -f kandidat.sql

echo "Adding kandidat_wahlkreis."
psql -U postgres -d Bundestagswahl -f kandidat_wahlkreis.sql

echo "Adding landesliste_kandidat. (Enter to start)"
psql -U postgres -d Bundestagswahl -f landesliste_kandidat.sql

echo "Adding wahlergebnis."
psql -U postgres -d Bundestagswahl -f wahlergebnis.sql

echo "Adding direktergebnis. (Enter to start)"
psql -U postgres -d Bundestagswahl -f direktergebnis.sql

echo "Adding listenergebnis."
psql -U postgres -d Bundestagswahl -f listenergebnis.sql

echo "Adding divisor."
psql -U postgres -d Bundestagswahl -f divisor.sql
