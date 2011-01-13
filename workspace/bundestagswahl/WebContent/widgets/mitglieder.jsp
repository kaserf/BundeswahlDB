<%@page import="data.Listenkandidatur"%>
<%@page import="data.Kandidat"%>
<%@page import="java.util.List"%>
<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
	Auswertung auswertung = new Auswertung();
	List<Kandidat> mitglieder = auswertung.getBundestagsMitglieder();
%>

<p>
<table id="mitgliederTable" class="tablesorter ui-corner-all"> 
<thead> 
<tr> 
    <th>Name</th>
    <th>Vorname</th> 
    <th>Partei</th>
    <th>Wahlkreis</th>
    <th>Landesliste</th>
    <th>Platz</th>
</tr> 
</thead> 
<tbody>
<%
	for  (Kandidat k : mitglieder) {
		int platz = 0;
		Listenkandidatur kandidatur = k.getListenKandidatur();
		if (kandidatur != null) {
			platz = kandidatur.getListenplatz();
		}
%>
<tr> 
    <td><%= k.getName() %></td>
    <td><%= k.getVorname() %></td> 
    <td><%= k.getPartei().getName() %></td>
    <td><%= k.getWahlkreis() != 0 ? k.getWahlkreis() : "-" %></td>
    <td><%= kandidatur != null ? kandidatur.getBundesland().getName() : "-" %></td>
    <td><%= platz != 0 ? platz : "-" %></td> 
</tr>
<%
	}
%>
 
</tbody> 
</table> 
<script>
$("#mitgliederTable").tablesorter( {
	sortList: [[0,0]]
});
</script>
</p>

