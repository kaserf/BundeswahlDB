<%@page import="data.Bundesland"%>
<%@page import="data.Partei"%>
<%@page import="data.Einzelergebnis"%>
<%@page import="data.Ueberhangmandate"%>
<%@page import="java.util.List"%>
<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%
	Auswertung auswertung = new Auswertung();
	List<Ueberhangmandate> mandate = auswertung.getUeberhangmandate();
%>

<div id='map_canvas'></div>

<% 
	String selectedBundesland = "Bayern";
	Ueberhangmandate blMandat = null;
	for (Ueberhangmandate mandat : mandate) {
		if (mandat.getBundesland().equals(selectedBundesland)) {
			blMandat = mandat;
			break;
		}
	}
%>

<div id="mandate">
	<table id="mandateTable" class="tablesorter ui-corner-all"> 
	<thead> 
	<tr> 
	    <th>Partei</th>
	    <th>Überhangmandate</th> 
	</tr> 
	</thead> 
	<tbody>
	<%
		for  (Einzelergebnis<Partei, Integer> parteiMandate : blMandat.getMandate()) {		
	%>
	<tr> 
	    <td><%= parteiMandate.getEntity() %></td>
	    <td><%= parteiMandate.getValue() %></td> 
	</tr>
	<%
		}
	%>
	</tbody> 
	</table>
</div>

<%
	List<Bundesland> bundeslaender = auswertung.getAllBundeslaender();
	int anzahlBl = bundeslaender.size();
%>

<script type='text/javascript'>
function drawMap() {
    window.alert("Test1");
	var data = new google.visualization.DataTable();
    data.addRows(<%= anzahlBl %>);
    data.addColumn('string', 'Kuerzel');
    data.addColumn('number', 'Hauptstadt');
    data.addColumn('string', 'Bundeslandname');
    
<%
	for (Bundesland bl : bundeslaender) {
		int index = bundeslaender.indexOf(bl);
%>
    data.setValue(<%= index %>, 0, '<%= "DE-" + bl.getKuerzel() %>');
    data.setValue(<%= index %>, 1, 2);
    data.setValue(<%= index %>, 2, '<%="Wadda" %>');
<%
	}
%>
    var options = {};
    options['dataMode'] = 'regions';
    options['region'] = 'DE';

    var container = document.getElementById('map_canvas');
    var geomap = new google.visualization.GeoMap(container);
    geomap.draw(data, options);
};
drawMap();

  </script>
