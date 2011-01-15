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
			$(this).click(function() {
				loadWidget(id);
			});
		});
	});
	
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
	<h2>Ergebnisse der Bundestagswahl</h2>
	<object width="480" height="385">
			<param name="movie" value="http://www.youtube.com/v/KmYKKVuEv5s?fs=1&amp;hl=de_DE"></param>
			<param name="allowFullScreen" value="true"></param>
			<param name="allowscriptaccess" value="always"></param>
			<embed src="http://www.youtube.com/v/KmYKKVuEv5s?fs=1&amp;hl=de_DE" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" width="480" height="385"></embed>
	</object>
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
		</p>
	</div>

</div>

</div>

</body>

</html>