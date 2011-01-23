<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="data.Bundesland"%>
<%@page import="data.Partei"%>
<%@page import="data.Einzelergebnis"%>
<%@page import="data.Ueberhangmandate"%>
<%@page import="java.util.List"%>
<%@page import="beans.Auswertung"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<div id='map_canvas' align="center"></div>

<div id="mandate"></div>

<%
	Auswertung auswertung = new Auswertung();
	List<Ueberhangmandate> mandate = auswertung.getUeberhangmandate();
	List<Bundesland> bundeslaender = auswertung.getAllBundeslaender();
	int anzahlBl = bundeslaender.size();
%>

<script type='text/javascript'>
	function drawMap() {
		var data = new google.visualization.DataTable();
	    data.addRows(<%= anzahlBl %>);
	    data.addColumn('string', 'Kuerzel');
	    data.addColumn('number', 'Überhangmandate');
	    data.addColumn('string', 'Bundeslandname');
    
<%
		Map<String, Integer> anzahlMandate = new HashMap<String, Integer>();
		for (Ueberhangmandate mandat : mandate) {
			int size = 0;
			for (Einzelergebnis<Partei, Integer> erg : mandat.getMandate()) {
				size += erg.getValue();
			}
			anzahlMandate.put(mandat.getBundesland().getKuerzel(), size);
		}
		for (Bundesland bl : bundeslaender) {
			int index = bundeslaender.indexOf(bl);
			int anzahl = anzahlMandate.get(bl.getKuerzel());
%>
		    data.setValue(<%= index %>, 0, '<%= "DE-" + bl.getKuerzel() %>');
		    data.setValue(<%= index %>, 1, <%= anzahl %>);
		    data.setValue(<%= index %>, 2, '<%= bl.getName() %>');
<%
		}
%>
	    var options = {};
	    options['dataMode'] = 'regions';
	    options['region'] = 'DE';
	    options['showLegend'] = false;
	
	    var container = document.getElementById('map_canvas');
	    var geomap = new google.visualization.GeoMap(container);
	    geomap.draw(data, options);
	    google.visualization.events.addListener(
	    	geomap, 'select', function() {
	    		geomap.setSelection(geomap.getSelection());
	    		var row = geomap.getSelection()[0].row;
	    		var selectedBl = data.getValue(row, 0);
	    		$('#mandate').fadeOut(function() {
	    			$('#mandate').load('widgets/ueberhangmandate/ueberhangmandattabelle.jsp?bundesland=' 
	    					+ selectedBl, function() {
						$('#mandate').fadeIn();
					});
	    		});
	    	}		
	    );
	};
	drawMap();

  </script>
