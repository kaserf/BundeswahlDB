<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.7.custom.css" rel="Stylesheet" />
<link type="text/css" href="css/tablesorter/style.css" rel="Stylesheet" />
<script type="text/javascript" src="js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.7.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.tablesorter.min.js"></script>
<script type='text/javascript' src='http://www.google.com/jsapi'></script>
<script type="text/javascript">

$(function() {
	$('#menu').buttonset();
	$('[name="selectRadio"]').each(function(index) {
		var id = $(this).attr('id');
		var value = $(this).attr('value');
		$(this).click(function() {
			if (value != 2 && value != 5 && value != 6) {
				var time = new Date().getTime();
				loadWidgetTime(id, time);
			}
			else {
				loadWidget(id);
				$('#benchmarkResults').html('');
			}
		});
	});	
});

function showBenchmark(millis) {
	$('#benchmarkResults').html('Bearbeitungszeit: ' + millis + ' ms');
}

function loadWidgetTime(name, time) {
	$('#content').fadeOut('normal', function() {
		$('#content').load('widgets/' + name + '.jsp', function() {
			$('#content').fadeIn('slow');
			var timeEnd = new Date().getTime();
			var diff = timeEnd - time;
			showBenchmark(diff);
		});	
	});
}

function loadWidget(name) {
	$('#content').fadeOut('normal', function() {
		$('#content').load('widgets/' + name + '.jsp', function() {
			$('#content').fadeIn('slow');
		});	
	});
}

google.load('visualization', '1', {'packages': ['geomap']});
</script>

<style>
body {
	margin-top: 20px;
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

#wrapper {
	width: 935px;
	margin: 0 auto;
}

#container {
}

#content {
	float: left;
	width: 700px;
}

#menu {
	float: right;
	width: 200px;
}

.centered {
	text-align: center;
}

label {
	width: 200px;
}

html { overflow: -moz-scrollbars-vertical; overflow-x: auto; }


</style>

<title>Bundestagswahl</title>
</head>
<body>
<div id="wrapper">

<div id="container">
	<div id="content">
	<h2 align="center">Ergebnisse der Bundestagswahl</h2>
	<div id="image" align="center" style="margin:40px">
		<img src="img/bundesadler_grau.gif" width="35%" height="35%" />
	</div>
	</div>

	<div id="menu">
		<p>
		<input type="radio" name="selectRadio" id="sitzverteilung" value="0"  />
			<label for="sitzverteilung">Sitzverteilung</label><br />
		<input type="radio" name="selectRadio" id="mitglieder" value="1"  />
			<label for="mitglieder">Mitglieder des Bundestages</label><br />
		<input type="radio" name="selectRadio" id="wahlkreisuebersicht" value="2"  />
			<label for="wahlkreisuebersicht">Wahlkreisübersicht</label><br />
		<input type="radio" name="selectRadio" id="wahlkreissieger" value="3"  />
			<label for="wahlkreissieger">Wahlkreissieger</label><br />
		<input type="radio" name="selectRadio" id="ueberhangmandate" value="4"  />
			<label for="ueberhangmandate">Überhangmandate</label><br />
		<input type="radio" name="selectRadio" id="knappstesieger" value="5"  />
			<label for="knappstesieger">Top 10 der knappsten Sieger</label><br />
		<input type="radio" name="selectRadio" id="stimmenabgabe" value="6"  />
			<label for="stimmenabgabe">Online Stimmenabgabe</label><br />
		</p>
		<div id="benchmarkResults">
		</div>
	</div>

</div>

</div>

</body>

</html>