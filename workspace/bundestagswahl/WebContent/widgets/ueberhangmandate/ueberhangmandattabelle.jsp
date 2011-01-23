<%@page import="data.Partei"%>
<%@page import="data.Einzelergebnis"%>
<%@page import="data.Ueberhangmandate"%>
<%@page import="java.util.List"%>
<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
	String bundesland = request.getParameter("bundesland");
	Auswertung auswertung = new Auswertung();
	List<Ueberhangmandate> mandate = auswertung.getUeberhangmandate();
	Ueberhangmandate blMandat = null;
	String bundeslandName = "";
	for (Ueberhangmandate mandat : mandate) {
		if (("DE-" + mandat.getBundesland().getKuerzel()).equals(bundesland)) {
			blMandat = mandat;
			bundeslandName = mandat.getBundesland().getName();
			break;
		}
	}
%>

	<h3>Überhangmandate in <%= bundeslandName %></h3>
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
	
<script>
	$("#mandateTable").tablesorter();
</script>