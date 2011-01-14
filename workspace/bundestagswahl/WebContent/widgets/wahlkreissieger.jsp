<%@page import="data.Partei"%>
<%@page import="data.Kandidat"%>
<%@page import="data.Einzelergebnis"%>
<%@page import="data.Wahlkreissieger"%>
<%@page import="java.util.List"%>
<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	Auswertung auswertung = new Auswertung();
	List<Wahlkreissieger> sieger = auswertung.getWahlkreisSieger();
%>
<p>
<div id="accordion">
    <h3><a href="#">Sieger Erststimmen</a></h3>
    <div>
    	<table id="erststimmenTable" class="tablesorter ui-corner-all">
		<thead>
		<tr>
			<th>Wahlkreisnummer</th>
			<th>Direktkandidat</th>
			<th>Partei</th>
			<th>Stimmen</th>
		</thead>
		<tbody>
<%
	for (Wahlkreissieger wksieger : sieger) {
		int nummer = wksieger.getWahlkreisNr();
		Einzelergebnis<Kandidat, Integer> erststimme = wksieger.getErstStimmenSieger();
		String name = erststimme.getEntity().getName() + ", " + erststimme.getEntity().getVorname();
		Partei partei = erststimme.getEntity().getPartei();
		String parteiName = "parteilos";
		if (partei != null) {
			parteiName = partei.getName();
		}
		int stimmen = erststimme.getValue();
%>
		<tr> 
    		<td><%= nummer %></td>
    		<td><%= name %></td> 
    		<td><%= parteiName %></td>
    		<td><%= stimmen %></td>
		</tr>
<% 
	}
%>
		</tbody>
		</table>
	</div>
    <h3><a href="#">Sieger Zweitstimmen</a></h3>
    <div>
    	<table id="zweitstimmenTable" class="tablesorter ui-corner-all">
		<thead>
		<tr>
			<th>Wahlkreisnummer</th>
			<th>Partei</th>
			<th>Stimmen</th>
		</thead>
		<tbody>
<%
	for (Wahlkreissieger wksieger : sieger) {
		int nummer = wksieger.getWahlkreisNr();
		Einzelergebnis<Partei, Integer> zweitstimme = wksieger.getZweitStimmenSieger();
		String parteiName = zweitstimme.getEntity().getName();
		int stimmen = zweitstimme.getValue();
%>
		<tr> 
    		<td><%= nummer %></td>
    		<td><%= parteiName %></td>
    		<td><%= stimmen %></td>
		</tr>
<% 
	}
%>
		</tbody>
		</table>
	</div>
</div>
</p>
<script language="javascript">
	$(function() {
		$( "#accordion" ).accordion({ autoHeight: false, collapsible: true });
	});
	$("#erststimmenTable").tablesorter( {
		sortList: [[0,0]]
	});
	$("#zweitstimmenTable").tablesorter( {
		sortList: [[0,0]]
	});
</script>