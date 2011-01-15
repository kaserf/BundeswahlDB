<%@page import="data.Kandidat"%>
<%@page import="data.Einzelergebnis"%>
<%@page import="java.util.ArrayList"%>
<%@page import="data.KnappsterSieger"%>
<%@page import="java.util.List"%>
<%@page import="beans.Auswertung"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
	
<%
	int id = Integer.parseInt(request.getParameter("partei"));	
	Auswertung auswertung = new Auswertung();
	List<KnappsterSieger> knappsteSieger = auswertung.getKnappsteSieger();
	String name = auswertung.getPartei(id).getName();
	List<KnappsterSieger> parteiSieger = new ArrayList<KnappsterSieger>();
	for (KnappsterSieger sieger : knappsteSieger) {
		if (sieger.getPartei().equals(name)) {
			parteiSieger.add(sieger);
		}
	}
	if (name.equals("pl")) {
		name = "parteilose";
	}
%>

<p>
<%
	int i = 1;
	for (KnappsterSieger sieger : parteiSieger) {
		Einzelergebnis<Kandidat, Integer> siegerErgebnis = sieger.getSieger();
		Kandidat siegerKandidat = siegerErgebnis.getEntity();
		Einzelergebnis<Kandidat, Integer> verliererErgebnis = sieger.getVerlierer();
		Kandidat verliererKandidat = verliererErgebnis.getEntity();
%>
	<div>
	<p>
		<span style="font-size: 18pt;"><%= i %>.</span> Wahlkreis <%= siegerKandidat.getWahlkreis()%>
		<div align="center" class="ui-state-highlight ui-widget" style="width:80%">
		<p>
		<span><%= siegerKandidat.getVorname() + " " + siegerKandidat.getName() 
			+ " (" + siegerKandidat.getPartei().getName() + ")" %></span>
		<b>
		<span><%= siegerErgebnis.getValue() + " : " + verliererErgebnis.getValue() %></span>
		</b>
		<span><%= verliererKandidat.getVorname() + " " + verliererKandidat.getName() 
			+ " (" + verliererKandidat.getPartei().getName() + ")" %></span>
		</p>
		</div>
	</p>
	</div>
<%
	i++;
	}
%>
</p>

<script>
	$('#parteiDisplay').html('<%= name %>');
</script>