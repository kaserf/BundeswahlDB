<%@page import="java.util.Collections"%>
<%@page import="data.Einzelergebnis"%>
<%@page import="com.googlecode.charts4j.GCharts"%>
<%@page import="com.googlecode.charts4j.PieChart"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.googlecode.charts4j.Slice"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="data.Partei"%>
<%@page import="java.util.Map"%>
<%@page import="beans.Auswertung"%>
<%
	Auswertung auswertung = new Auswertung();
	List<Einzelergebnis<Partei, Integer>> sitzverteilung = auswertung.getSitzverteilung().getParteiSitze();
	Collections.sort(sitzverteilung);
	Collections.reverse(sitzverteilung);
	List<Slice> slices = new ArrayList<Slice>();
	for (Einzelergebnis<Partei, Integer> erg : sitzverteilung) {
		String parteiName = erg.getEntity().getName();
		int sitze = erg.getValue();
		Slice s = Slice.newSlice(sitze, null, parteiName + " (" + sitze + ")", null);
		slices.add(s);
	}
	PieChart chart = GCharts.newPieChart(slices);
	chart.setSize(700,300);
	chart.setMargins(0,0,0,0);
	String url = chart.toURLString();
%>
<p class="centered">
	<img id="SV" src="<%= url %>" alt="Sitzverteilung" />
</p>
<p>
	<table id="sitzTable" class="tablesorter ui-corner-all"> 
	<thead> 
	<tr> 
	    <th>Partei</th> 
	    <th>Sitze</th> 
	</tr> 
	</thead> 
	<tbody>
	<%
		for (Einzelergebnis<Partei, Integer> erg : sitzverteilung) {
	%>
	<tr> 
	    <td><%= erg.getEntity().getName() %></td> 
	    <td><%= erg.getValue() %></td> 
	</tr>
	<%
		}
	%>
	 
	</tbody> 
	</table> 
</p>
<script>
	$("#sitzTable").tablesorter( {
		sortList: [[1,1]]
	});
</script>